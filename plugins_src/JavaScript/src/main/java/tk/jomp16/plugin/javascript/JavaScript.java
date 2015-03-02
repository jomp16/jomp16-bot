/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.javascript;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.irc.event.events.InitEvent;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.help.HelpRegister;
import tk.jomp16.plugin.level.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStreamReader;
import java.net.URL;

@Log4j2
public class JavaScript extends PluginEvent {
    private ScriptEngine scriptEngine;

    @Override
    public void onInit(InitEvent initEvent) {
        scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

        initEvent.addHelp(new HelpRegister("js", new String[]{"javascript"}, "Run a java/javascript command", "-url the url or java/javascript command", Level.OWNER));
    }

    @Command(value = "js", optCommands = "javascript", level = Level.OWNER, args = {"url:"})
    public void js(CommandEvent commandEvent) {
        if (commandEvent.getMessage().length() > 0 || commandEvent.getArgs().size() >= 1) {
            scriptEngine.put("commandEvent", commandEvent);

            if (commandEvent.getOptionSet().has("url")) {
                try {
                    String jsOutput = String.valueOf(scriptEngine.eval(new InputStreamReader(new URL((String) commandEvent.getOptionSet().valueOf("url")).openStream())));

                    if (jsOutput != null) {
                        if (!jsOutput.equals("null")) {
                            commandEvent.respond(jsOutput, false);
                        }
                    }
                } catch (Exception e) {
                    commandEvent.respond(e.getMessage());
                }
            } else {
                try {
                    String jsOutput = String.valueOf(scriptEngine.eval(commandEvent.getMessage()));

                    if (jsOutput != null) {
                        if (!jsOutput.equals("null")) {
                            commandEvent.respond(jsOutput, false);
                        }
                    }
                } catch (Exception e) {
                    commandEvent.respond(e.getMessage());
                }
            }
        } else {
            commandEvent.showUsage(commandEvent.getCommand());
        }
    }
}
