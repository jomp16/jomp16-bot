/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.listener.listeners;

import lombok.Getter;
import lombok.Setter;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.handlers.CtcpHandler;
import tk.jomp16.irc.listener.Listener;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.event.Event;

import java.io.File;

@Setter
@Getter
public class DccFileSendListener extends Listener {
    private String fileName;
    private long bytes;
    private String ip;
    private int port;

    public DccFileSendListener(IrcManager ircManager, User user, Channel channel, Event event) {
        super(ircManager, user, channel, event);
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
