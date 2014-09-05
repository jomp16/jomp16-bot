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
import tk.jomp16.irc.listener.Listener;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.event.Event;

@Getter
@Setter
public class KickListener extends Listener {
    private User userKicked;
    private String reason;

    public KickListener(IrcManager ircManager, User user, Channel channel, Event event) {
        super(ircManager, user, channel, event);
    }
}
