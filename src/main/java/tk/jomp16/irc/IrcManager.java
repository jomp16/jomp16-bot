/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.magicwerk.brownies.collections.GapList;
import tk.jomp16.configuration.Configuration;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.event.events.DisableEvent;
import tk.jomp16.irc.event.events.InitEvent;
import tk.jomp16.irc.handler.handlers.PrivMsgHandler;
import tk.jomp16.irc.netty.NettyDecoder;
import tk.jomp16.irc.netty.NettyEncoder;
import tk.jomp16.irc.netty.NettyHandler;
import tk.jomp16.irc.output.OutputCap;
import tk.jomp16.irc.output.OutputCtcp;
import tk.jomp16.irc.output.OutputIrc;
import tk.jomp16.irc.output.OutputRaw;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.user.UserList;
import tk.jomp16.plugin.Plugin;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.PluginManager;
import tk.jomp16.plugin.about.About;
import tk.jomp16.plugin.command.Commands;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.help.Help;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@Getter
public class IrcManager {
    private IrcManager ircManager;
    private Configuration configuration;
    private PluginManager pluginManager;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    private Channel channel;
    private OutputRaw outputRaw;
    private OutputIrc outputIrc;
    private OutputCtcp outputCtcp;
    private OutputCap outputCap;
    private IrcParser ircParser;
    private UserList userList;
    private ChannelList channelList;
    private ServerInfo serverInfo;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private List<PluginEvent> pluginEvents = new GapList<>();
    private Map<String, PluginInfo> pluginInfoHashMap = new HashMap<>();
    private Multimap<String, PluginEvent> eventMultimap = HashMultimap.create();

    public IrcManager(Configuration configuration) throws Exception {
        this.configuration = configuration;
        this.ircManager = this;
        this.pluginManager = new PluginManager(this);

        registerEvent(new About());
        registerEvent(new Help());
        registerEvent(new Commands());
        registerEvent(pluginManager);

        pluginManager.loadAll();
    }

    public void restart() throws Exception {
        shutdown();
        startIrc();
    }

    public void shutdown() throws Exception {
        this.executor.shutdownNow();
        this.workerGroup.shutdownGracefully();
        this.executor = Executors.newCachedThreadPool();
    }

    public void startIrc() throws Exception {
        log.info("Starting IRC client");

        try {
            this.ircParser = new IrcParser();
            this.channelList = new ChannelList(this);
            this.userList = new UserList(this);
            this.serverInfo = new ServerInfo();

            this.bootstrap = new Bootstrap();
            this.workerGroup = new NioEventLoopGroup();

            this.bootstrap.group(workerGroup);
            this.bootstrap.channel(NioSocketChannel.class);
            this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(300, 0, 0),
                            new LineBasedFrameDecoder(512),
                            new NettyDecoder(CharsetUtil.UTF_8),
                            new NettyEncoder(CharsetUtil.UTF_8),
                            new NettyHandler(ircManager));
                }
            });

            this.channelFuture = this.bootstrap.connect(configuration.getServer(), configuration.getPort()).sync();

            this.channel = this.channelFuture.channel();
            this.outputRaw = new OutputRaw(this);
            this.outputIrc = new OutputIrc(this);
            this.outputCtcp = new OutputCtcp(this);
            this.outputCap = new OutputCap(this);

            if (this.configuration.isSasl()) {
                outputCap.sendList();
            }

            this.outputRaw.writeRaw("NICK " + this.configuration.getNick());
            this.outputRaw.writeRaw("USER " + this.configuration.getIdent() + " 8 * :" + this.configuration.getRealName());

            this.channelFuture.channel().closeFuture().sync();
        } finally {
            this.workerGroup.shutdownGracefully();
        }
    }

    public void joinAllChannels() {
        configuration.getChannels().forEach(outputIrc::joinChannel);
    }

    public void registerEvent(PluginEvent pluginEvent) {
        log.debug("Loading plugin: " + pluginEvent.getClass().getSimpleName() + "...");

        pluginEvents.add(pluginEvent);
        eventMultimap.put(pluginEvent.getClass().getSimpleName(), pluginEvent);

        try {
            pluginEvent.onInit(new InitEvent(this, null, null, pluginEvent));
        } catch (Exception e) {
            log.error(e, e);
        }

        PrivMsgHandler.addCommandsFromEvent(pluginEvent);
        Help.addHelp(pluginEvent);
    }

    public void unregisterPlugin(Plugin plugin) {
        if (pluginInfoHashMap.containsKey(plugin.getPluginInfo().getName())) {
            pluginInfoHashMap.remove(plugin.getPluginInfo().getName());

            plugin.getPluginEvents().forEach(event -> {
                try {
                    event.onDisable(new DisableEvent(this, null, null, event));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                pluginEvents.remove(event);
                eventMultimap.removeAll(plugin.getPluginInfo().getName());
                PrivMsgHandler.removeCommandsFromEventName(event);
                Help.removeHelp(event);
            });
        }
    }

    public void registerPlugin(Plugin plugin) {
        if (plugin.getPluginEvents() != null && plugin.getPluginEvents().size() != 0) {
            log.debug("Loading plugin jar: " + plugin.getPluginInfo().getName() + "...");

            pluginEvents.addAll(plugin.getPluginEvents());
            eventMultimap.putAll(plugin.getPluginInfo().getName(), plugin.getPluginEvents());
            pluginInfoHashMap.put(plugin.getPluginInfo().getName(), plugin.getPluginInfo());

            plugin.getPluginEvents().forEach(event -> {
                try {
                    event.onInit(new InitEvent(this, null, null, event));
                } catch (Exception e) {
                    log.error("An error happened when trying to init plugin!", e);
                }

                PrivMsgHandler.addCommandsFromEvent(event);
                Help.addHelp(event);
            });
        }
    }

    public PluginInfo getPluginInfoFromEvent(PluginEvent pluginEvent) {
        for (Plugin plugin : pluginManager.getPlugins()) {
            for (PluginEvent pluginEvent1 : plugin.getPluginEvents()) {
                if (pluginEvent1.equals(pluginEvent)) {
                    return plugin.getPluginInfo();
                }
            }
        }

        return null;
    }

    public void proceedSaslAuth(int step) {
        switch (step) {
            case 0:
                ircManager.getOutputCap().sendReq("sasl");
                break;
            case 1:
                ircManager.getOutputRaw().writeRaw("AUTHENTICATE PLAIN");
                break;
            case 2:
                ircManager.getOutputRaw().writeRaw("AUTHENTICATE " + new String(Base64.getEncoder().encode((configuration.getSaslUser() + "\0" + configuration.getSaslUser() + "\0" + configuration.getPassword()).getBytes())));
                break;
        }
    }
}
