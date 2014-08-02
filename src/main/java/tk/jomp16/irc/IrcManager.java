package tk.jomp16.irc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.configuration.Configuration;
import tk.jomp16.irc.handler.handlers.PrivMsgHandler;
import tk.jomp16.irc.listener.listeners.DisableListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.irc.netty.NettyDecoder;
import tk.jomp16.irc.netty.NettyEncoder;
import tk.jomp16.irc.netty.NettyHandler;
import tk.jomp16.irc.output.OutputCap;
import tk.jomp16.irc.output.OutputCtcp;
import tk.jomp16.irc.output.OutputIrc;
import tk.jomp16.irc.output.OutputRaw;
import tk.jomp16.irc.user.UserList;
import tk.jomp16.plugin.Plugin;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.PluginManager;
import tk.jomp16.plugin.about.About;
import tk.jomp16.plugin.command.Commands;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.help.Help;

import java.util.*;
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
    private ExecutorService executor = Executors.newCachedThreadPool();
    private List<Event> events = new ArrayList<>();
    private Map<String, PluginInfo> pluginInfoHashMap = new HashMap<>();
    private Multimap<String, Event> eventMultimap = HashMultimap.create();

    public IrcManager(Configuration configuration) throws Exception {
        this.configuration = configuration;
        this.ircManager = this;
        this.pluginManager = new PluginManager(this);

        PrivMsgHandler.setIrcManager(this);
        UserList.setIrcManager(this);

        registerEvent(new About());
        registerEvent(new Help());
        registerEvent(new Commands());
        registerEvent(pluginManager);

        pluginManager.loadAll();
    }

    public void startIrc() throws Exception {
        log.info("Starting IRC client");

        try {
            this.bootstrap = new Bootstrap();
            this.workerGroup = new NioEventLoopGroup();

            this.bootstrap.group(workerGroup);
            this.bootstrap.channel(NioSocketChannel.class);
            this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(512),
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

    public void registerEvent(Event event) {
        events.add(event);
        eventMultimap.put(event.getClass().getSimpleName(), event);

        Runnable runnable = () -> {
            try {
                event.onInit(new InitListener(this, null, null, event, null));
            } catch (Exception e) {
                log.error(e, e);
            }

            PrivMsgHandler.addCommandsFromEvent(event);
            Help.addHelp(event);
        };

        executor.execute(runnable);
    }

    public void unregisterPlugin(Plugin plugin) {
        if (pluginInfoHashMap.containsKey(plugin.getPluginInfo().getName())) {
            pluginInfoHashMap.remove(plugin.getPluginInfo().getName());

            Runnable runnable = () -> plugin.getEvents().forEach(event -> {
                try {
                    event.onDisable(new DisableListener(this, null, null, event, null));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                events.remove(event);
                eventMultimap.removeAll(plugin.getPluginInfo().getName());
                PrivMsgHandler.removeCommandsFromEventName(event);
                Help.removeHelp(event);
            });

            executor.execute(runnable);
        }
    }

    public void registerPlugin(Plugin plugin) {
        if (plugin.getEvents() != null && plugin.getEvents().size() != 0) {
            events.addAll(plugin.getEvents());
            eventMultimap.putAll(plugin.getPluginInfo().getName(), plugin.getEvents());
            pluginInfoHashMap.put(plugin.getPluginInfo().getName(), plugin.getPluginInfo());

            Runnable runnable = () -> plugin.getEvents().forEach(event -> {
                try {
                    event.onInit(new InitListener(this, null, null, event, null));
                } catch (Exception e) {
                    log.error("An error happened when trying to init plugin!", e);
                }

                PrivMsgHandler.addCommandsFromEvent(event);
                Help.addHelp(event);
            });

            executor.execute(runnable);
        }
    }

    public PluginInfo getPluginInfoFromEvent(Event event) {
        for (Plugin plugin : pluginManager.getPlugins()) {
            for (Event event1 : plugin.getEvents()) {
                if (event1.equals(event)) {
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
