/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.ram;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.utils.Utils;

public class Ram extends Event {
    @Command("ram")
    public void ram(CommandListener commandListener) {
        commandListener.respond(Utils.getRamUsage());
    }
}
