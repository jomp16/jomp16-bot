/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import tk.jomp16.irc.IrcManager;

@RequiredArgsConstructor
public class OutputRaw {
    private final IrcManager ircManager;

    @Synchronized
    public void writeRaw(@NonNull Object msg) {
        ircManager.getChannelFuture().channel().writeAndFlush(msg);
    }
}
