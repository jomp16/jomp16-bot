/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.javascript;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.help.HelpRegister;
import tk.jomp16.plugin.level.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStreamReader;
import java.net.URL;

@Log4j2
public class JavaScript extends Event {
    private ScriptEngine scriptEngine;

    @Override
    public void onInit(InitListener initListener) {
        scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

        initListener.addHelp(new HelpRegister("js", new String[]{"javascript"}, "Run a java/javascript command", "-url the url or java/javascript command", Level.OWNER));
    }

    @Command(value = "js", optCommands = "javascript", level = Level.OWNER, args = {"url:"})
    public void js(CommandListener commandListener) {
        if (commandListener.getMessage().length() > 0 || commandListener.getArgs().size() >= 1) {
            scriptEngine.put("commandListener", commandListener);

            if (commandListener.getOptionSet().has("url")) {
                try {
                    Object object = scriptEngine.eval(new InputStreamReader(
                            new URL((String) commandListener.getOptionSet().valueOf("url")).openStream()));

                    if (object != null) {
                        if (!object.equals("null")) {
                            commandListener.respond(object, false);
                        }
                    }
                } catch (Exception e) {
                    commandListener.respond(e.getMessage());
                }
            } else {
                try {
                    Object object = scriptEngine.eval(commandListener.getMessage());

                    if (object != null) {
                        if (!object.equals("null")) {
                            commandListener.respond(object, false);
                        }
                    }
                } catch (Exception e) {
                    commandListener.respond(e.getMessage());
                }
            }
        } else {
            commandListener.showUsage(commandListener.getCommand());
        }
    }
}
