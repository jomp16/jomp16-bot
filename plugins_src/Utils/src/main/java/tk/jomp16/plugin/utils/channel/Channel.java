/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.channel;

import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.level.Level;

public class Channel extends PluginEvent {
    @Command(value = "join", level = Level.OWNER)
    public void join(CommandEvent commandEvent) {
        if (commandEvent.getArgs().size() > 0) {
            commandEvent.getIrcManager().getOutputIrc().joinChannel(commandEvent.getArgs().get(0));
            commandEvent.respond("Done!");
        }
    }

    @Command(value = "part", level = Level.OWNER)
    public void part(CommandEvent commandEvent) {
        if (commandEvent.getArgs().size() > 0) {
            commandEvent.getIrcManager().getOutputIrc().partChannel(commandEvent.getArgs().get(0));
            commandEvent.respond("Done!");
        }
    }
}
