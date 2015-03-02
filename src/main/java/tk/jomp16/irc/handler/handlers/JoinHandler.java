/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.event.events.JoinEvent;
import tk.jomp16.irc.user.User;

@RequiredArgsConstructor
@Log4j2
public class JoinHandler implements Handler {
    private final IrcManager ircManager;
    private final User user;
    private final Channel channel;

    @Override
    public void handle() {
        Runnable runnable = () -> ircManager.getPluginEvents().forEach((event) -> {
            try {
                event.onJoin(new JoinEvent(ircManager, user, channel, event));
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });

        ircManager.getExecutor().execute(runnable);
    }
}
