/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.channel;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.level.Level;

public class Channel extends Event {
    @Command(value = "join", level = Level.OWNER)
    public void join(CommandListener commandListener) {
        if (commandListener.getArgs().size() > 0) {
            commandListener.getIrcManager().getOutputIrc().joinChannel(commandListener.getArgs().get(0));
            commandListener.respond("Done!");
        }
    }

    @Command(value = "part", level = Level.OWNER)
    public void part(CommandListener commandListener) {
        if (commandListener.getArgs().size() > 0) {
            commandListener.getIrcManager().getOutputIrc().partChannel(commandListener.getArgs().get(0));
            commandListener.respond("Done!");
        }
    }
}
