/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.utils;

import com.google.common.base.CharMatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.magicwerk.brownies.collections.GapList;

import java.io.File;
import java.util.List;

public class Utils {
    /**
     * Tokenize IRC raw input into it's components, keeping the
     * 'sender' and 'message' fields intact.
     *
     * @param input A string in the format [:]item [item] ... [:item [item] ...]
     * @return List of strings.
     * @author PircBotX team
     */
    public static List<String> tokenizeLine(String input) {
        List<String> stringParts = new GapList<>();
        if (input == null || input.length() == 0) {
            return stringParts;
        }

        // Heavily optimized version string split by space with all characters after :
        // added as a single entry. Under benchmarks, its faster than StringTokenizer,
        // String.split, toCharArray, and charAt
        String trimmedInput = CharMatcher.WHITESPACE.trimFrom(input);
        int pos = 0, end;
        while ((end = trimmedInput.indexOf(' ', pos)) >= 0) {
            stringParts.add(trimmedInput.substring(pos, end));
            pos = end + 1;
            if (trimmedInput.charAt(pos) == ':') {
                stringParts.add(trimmedInput.substring(pos + 1));
                return stringParts;
            }
        }

        // No more spaces, add last part of line
        stringParts.add(trimmedInput.substring(pos));
        return stringParts;
    }

    public static List<String> tokenizeDccRequest(String request) {
        int quotesIndexBegin = request.indexOf('"');

        if (quotesIndexBegin == -1) {
            // Just use tokenizeLine
            return Utils.tokenizeLine(request);
        }

        // This is a slightly modified version of Utils.tokenizeLine to parse
        // potential quotes in file names
        int quotesIndexEnd = request.lastIndexOf('"');
        List<String> stringParts = new GapList<>();
        int pos = 0, end;
        while ((end = request.indexOf(' ', pos)) >= 0) {
            if (pos >= quotesIndexBegin && end < quotesIndexEnd) {
                // We've entered the filename. Add and skip
                stringParts.add(request.substring(quotesIndexBegin, quotesIndexEnd + 1));
                pos = quotesIndexEnd + 2;
                continue;
            }

            stringParts.add(request.substring(pos, end));
            pos = end + 1;

            if (request.charAt(pos) == ':') {
                stringParts.add(request.substring(pos + 1));
                return stringParts;
            }
        }

        // No more spaces, add last part of line
        stringParts.add(request.substring(pos));

        return stringParts;
    }


    public static String getRamUsage() {
        return humanReadableByteCount(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), true);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void downloadDccFileStream(File f, String ip, int port, long bytes, long startPosition) throws InterruptedException {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DccFileSendNettyDecoder(f, startPosition), new DccFileSendNettyEncoder());
                }
            });
            bootstrap.connect(ip, port).sync().channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
