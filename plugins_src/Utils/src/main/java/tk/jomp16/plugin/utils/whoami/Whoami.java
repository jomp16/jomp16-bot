/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.whoami;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;

public class Whoami extends Event {
    @Command("whoami")
    public void whoami(CommandListener commandListener) {
        commandListener.respond("You're: " + commandListener.getUser().getNickName() + ", and your level is: " + commandListener.getUser().getLevel().toString().toLowerCase(), false);
    }
}
