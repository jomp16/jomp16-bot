/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.event.events;

import joptsimple.OptionSet;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.event.Event;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.help.HelpRegister;

import java.util.List;

@Getter
@Setter
@Log4j2
public class CommandEvent extends Event {
    private String message;
    private List<String> args;
    private String command;
    private OptionSet optionSet;
    private String rawMessage;

    public CommandEvent(IrcManager ircManager, User user, Channel channel, PluginEvent pluginEvent) {
        super(ircManager, user, channel, pluginEvent);
    }

    @Synchronized
    public synchronized void showUsage(String command) {
        for (HelpRegister helpRegister : pluginEvent.getHelpRegister()) {
            if (helpRegister.getCommand().equals(command)) {
                String usage = helpRegister.getUsage();

                if (usage != null) {
                    respond("Usage: " + ircManager.getConfiguration().getPrefix() + command + " " + helpRegister.getUsage());
                } else {
                    respond("Usage: " + ircManager.getConfiguration().getPrefix());
                }
            } else {
                if (helpRegister.getOptCommands() != null && helpRegister.getOptCommands().length != 0) {
                    for (String s : helpRegister.getOptCommands()) {
                        if (s.equals(command)) {
                            String usage = helpRegister.getUsage();
                            if (usage != null) {
                                respond("Usage: " + ircManager.getConfiguration().getPrefix() + s + " " + helpRegister.getUsage());
                            } else {
                                respond("Usage: " + ircManager.getConfiguration().getPrefix() + s);
                            }
                        }
                    }
                }
            }
        }
    }
}
