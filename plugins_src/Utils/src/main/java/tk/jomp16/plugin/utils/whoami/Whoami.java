/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.whoami;

import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;

public class Whoami extends PluginEvent {
    @Command("whoami")
    public void whoami(CommandEvent commandEvent) {
        commandEvent.respond("You're: " + commandEvent.getUser().getNickName() + ", and your level is: " + commandEvent.getUser().getLevel().toString().toLowerCase(), false);
    }
}
