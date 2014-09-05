/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.handler.handlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.DccFileSendListener;
import tk.jomp16.irc.parser.parsers.CtcpParser;
import tk.jomp16.irc.user.User;
import tk.jomp16.utils.Utils;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@ToString
public class CtcpHandler implements Handler {
    @Getter
    private static Map<String, DccFileSendHolder> dccFileSendHolderMap = new HashMap<>();
    private final IrcManager ircManager;
    private final User user;
    private final Channel channel;
    private final CtcpParser.CtcpCommand ctcpCommand;
    private final String raw;

    public static void transferFile(IrcManager ircManager, User user, File f, String fileName, long bytes, String ip, int port) {
        DccFileSendHolder dccFileSendHolder = new DccFileSendHolder(f, user, fileName, bytes, ip, port);
        String uuid = UUID.randomUUID().toString();

        dccFileSendHolderMap.put(uuid, dccFileSendHolder);

        if (f.exists()) {
            if (f.length() < bytes) {
                dccFileSendHolder.setPosition(f.length());

                ircManager.getOutputCtcp().sendResponsePrivMsgEvent(user.getNickName(), "DCC RESUME \"" + fileName + "\" " + port + " " + f.length());
            }
        } else {
            try {
                Utils.downloadDccFileStream(f, ip, port, bytes, 0);

                dccFileSendHolderMap.remove(uuid);
            } catch (InterruptedException e) {
                log.error("Dcc file send!", e);
            }
        }
    }

    @Override
    public void respond() {
        switch (ctcpCommand) {
            case TIME:
                ircManager.getOutputCtcp().sendResponseNotice(channel.getTargetName(), "TIME " + Instant.now());
                break;
            case DCC:
                // todo
                // CtcpHandler(ircManager=tk.jomp16.irc.IrcManager@35c6d48d, user=User(nickName=jomp16, hostName=~jomp16, hostMask=unaffiliated/jomp16, realName=José Olivio Pedrosa, level=OWNER), channel=Channel(ircManager=tk.jomp16.irc.IrcManager@35c6d48d, targetName=jomp16), ctcpCommand=DCC, raw=DCC SEND 768_multirom_dark.zip 0 45983 1127415)
                List<String> parsedInput = Utils.tokenizeDccRequest(raw.replace("\001", ""));

                if (parsedInput.get(1).equals("SEND")) {
                    Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
                        try {
                            DccFileSendListener dccFileSendListener = new DccFileSendListener(ircManager, user, channel, event);
                            dccFileSendListener.setFileName(parsedInput.get(2).replace("\"", ""));
                            dccFileSendListener.setIp(parsedInput.get(3));
                            dccFileSendListener.setPort(Integer.parseInt(parsedInput.get(4)));
                            dccFileSendListener.setBytes(Long.parseLong(parsedInput.get(5)));

                            event.onDccFileSend(dccFileSendListener);
                        } catch (Exception e) {
                            log.error("An error happened!", e);
                        }
                    });

                    ircManager.getExecutor().execute(runnable);
                } else if (parsedInput.get(1).equals("RESUME")) {
                    // todo
                } else if (parsedInput.get(1).equals("ACCEPT")) {
                    String fileName = parsedInput.get(2).replace("\"", "");
                    int port = Integer.parseInt(parsedInput.get(3));
                    long position = Long.parseLong(parsedInput.get(4));

                    final String[] uuid = new String[1];

                    dccFileSendHolderMap.entrySet().parallelStream().filter(entry -> entry.getValue().fileName.equals(fileName) && entry.getValue().port == port && entry.getValue().position == position).forEach(entry -> {
                        try {
                            Utils.downloadDccFileStream(entry.getValue().file, entry.getValue().ip, port, entry.getValue().bytes, entry.getValue().position);

                            uuid[0] = entry.getKey();
                        } catch (InterruptedException e) {
                            log.error("Dcc file send!", e);
                        }
                    });

                    dccFileSendHolderMap.remove(uuid[0]);
                }
                break;
            case PING:
                ircManager.getOutputCtcp().sendResponseNotice(channel.getTargetName(), raw.replace("\001", ""));
                break;
            case VERSION:
                ircManager.getOutputCtcp().sendResponseNotice(channel.getTargetName(), "VERSION " + getClass().getPackage().getImplementationTitle() + " " + getClass().getPackage().getImplementationVersion() + " | source code at https://github.com/jomp16/jomp16-bot");
                break;
        }
    }

    @RequiredArgsConstructor
    private static class DccFileSendHolder {
        private final File file;
        private final User user;
        private final String fileName;
        private final long bytes;
        private final String ip;
        private final int port;
        @Setter
        private long position = 0;
    }
}
