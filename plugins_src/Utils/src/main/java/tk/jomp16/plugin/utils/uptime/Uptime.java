/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.uptime;

import org.apache.commons.lang3.time.DurationFormatUtils;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;

import java.lang.management.ManagementFactory;

public class Uptime extends Event {
    @Command("uptime")
    public void uptime(CommandListener commandListener) {
        commandListener.respond("Uptime: " + DurationFormatUtils.formatDurationWords(ManagementFactory.getRuntimeMXBean().getUptime(), true, false));
    }
}