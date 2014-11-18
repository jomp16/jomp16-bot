/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.output;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import tk.jomp16.irc.IrcManager;

import java.io.File;

@RequiredArgsConstructor
public class OutputCtcp {
    private final IrcManager ircManager;

    @Synchronized
    public void sendAction(String target, String action) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :\001", "ACTION " + action, "\001");
    }

    @Synchronized
    public void sendTime(String target) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :\001TIME\001");
    }

    @Synchronized
    public void sendResponsePrivMsgEvent(String target, String response) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :\001", response, "\001");
    }

    @Synchronized
    public void sendResponseNotice(String target, String response) {
        ircManager.getOutputRaw().writeRaw("NOTICE " + target + " :\001", response, "\001");
    }

    @Synchronized
    public void sendFile(String target, File f) throws Exception {

    }
}
