/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.handler.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.QuitListener;
import tk.jomp16.irc.user.User;

@RequiredArgsConstructor
@Log4j2
public class QuitHandler implements Handler {
    private final IrcManager ircManager;
    private final User user;
    private final String reason;

    @Override
    public void respond() {
        Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
            try {
                QuitListener quitListener = new QuitListener(ircManager, user, null, event);
                quitListener.setReason(reason);

                event.onQuit(quitListener);
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });

        ircManager.getExecutor().execute(runnable);
    }
}
