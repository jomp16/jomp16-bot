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

    // TODO: REWRITE THIS
    @Synchronized
    public void writeRaw(@NonNull String msg) {
        ircManager.getChannelFuture().channel().writeAndFlush(msg);
    }

    @Synchronized
    public void writeRaw(@NonNull String prefix, @NonNull String message) {
        writeRaw(prefix, message, "");
    }

    @Synchronized
    public void writeRaw(@NonNull String prefix, @NonNull String message, @NonNull String suffix) {
        String finalMessage = prefix + message + suffix;

        int realMaxLineLength = 512 - 2;

        if (finalMessage.length() < realMaxLineLength) {
            //Length is good (or auto split message is false), just go ahead and send it
            writeRaw(finalMessage);
            return;
        }

        // Too long, split it up
        int maxMessageLength = realMaxLineLength - (prefix + suffix).length();

        // Oh look, no function to split every nth char. Since regex is expensive, use this nonsense
        int iterations = (int) Math.ceil(message.length() / (double) maxMessageLength);

        for (int i = 0; i < iterations; i++) {
            int endPoint = (i != iterations - 1) ? ((i + 1) * maxMessageLength) : message.length();
            String curMessagePart = prefix + message.substring(i * maxMessageLength, endPoint) + suffix;
            writeRaw(curMessagePart);
        }
    }
}
