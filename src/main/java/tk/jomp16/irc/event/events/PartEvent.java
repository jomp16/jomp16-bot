/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.event.events;

import lombok.Getter;
import lombok.Setter;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.event.Event;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.event.PluginEvent;

@Getter
@Setter
public class PartEvent extends Event {
    private String reason;

    public PartEvent(IrcManager ircManager, User user, Channel channel, PluginEvent pluginEvent) {
        super(ircManager, user, channel, pluginEvent);
    }
}
