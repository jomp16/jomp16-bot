/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.lmgtfy;

import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.irc.event.events.InitEvent;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;

import java.net.URLEncoder;

public class Lmgtfy extends PluginEvent {
    private String URL = "http://lmgtfy.com/?q=%s";

    @Command(value = "lmgtfy", args = {"user:", "term:"})
    public void lmgtfy(CommandEvent commandEvent) throws Exception {
        if (commandEvent.getOptionSet().has("user") && commandEvent.getOptionSet().has("term")) {
            String formatted = String.format(URL, URLEncoder.encode((String) commandEvent.getOptionSet().valueOf("term"), "UTF-8"));

            commandEvent.respond((String) commandEvent.getOptionSet().valueOf("user"), formatted, true);
        } else {
            // todo: help
        }
    }

    @Override
    public void onInit(InitEvent initEvent) {
        // todo: help
    }
}
