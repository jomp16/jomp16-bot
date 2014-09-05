/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;

@RequiredArgsConstructor
public class PingHandler implements Handler {
    private final IrcManager ircManager;
    private final String target;

    @Override
    public void respond() {
        ircManager.getOutputRaw().writeRaw("PONG :" + target);
    }
}
