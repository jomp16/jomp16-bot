/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.event.events;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.event.Event;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.event.PluginEvent;

@Log4j2
public class DisableEvent extends Event {
    public DisableEvent(IrcManager ircManager, User user, Channel channel, PluginEvent pluginEvent) {
        super(ircManager, user, channel, pluginEvent);
    }
}
