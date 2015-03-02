/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.event.events;

import lombok.Getter;
import lombok.Setter;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.handlers.CtcpHandler;
import tk.jomp16.irc.event.Event;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.event.PluginEvent;

import java.io.File;

@Setter
@Getter
public class DccFileSendEvent extends Event {
    private String fileName;
    private long bytes;
    private String ip;
    private int port;

    public DccFileSendEvent(IrcManager ircManager, User user, Channel channel, PluginEvent pluginEvent) {
        super(ircManager, user, channel, pluginEvent);
    }

    public void accept() {
        File f = new File("DCC/DOWNLOADS");

        if (!f.exists()) {
            f.mkdirs();
        }

        f = new File(f, fileName);

        CtcpHandler.transferFile(ircManager, user, f, fileName, bytes, ip, port);
    }

    public void accept(File out) {
        CtcpHandler.transferFile(ircManager, user, out, fileName, bytes, ip, port);
    }
}
