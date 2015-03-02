/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.handler.handlers;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.magicwerk.brownies.collections.GapList;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.irc.event.events.PrivMsgEvent;
import tk.jomp16.irc.parser.Source;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.command.Commands;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.level.Level;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Log4j2
public class PrivMsgHandler implements Handler {
    private static List<EventRegister> eventRegisters = new GapList<>();
    private final IrcManager ircManager;
    private final User user;
    private final Channel channel;
    private final String message;
    private List<String> args = new GapList<>();

    public static void addCommandsFromEvent(PluginEvent pluginEvent) {
        Method[] methods = pluginEvent.getClass().getDeclaredMethods();

        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(Command.class);
            if (annotation != null) {
                method.setAccessible(true);
                Command command = (Command) annotation;
                EventRegister eventRegister = new EventRegister(command.value(), command.optCommands(), command.args(), method, pluginEvent, command.level());
                eventRegisters.add(eventRegister);
                Commands.addCommand(eventRegister);
            }
        }
    }

    public static void removeCommandsFromEventName(PluginEvent pluginEvent) {
        eventRegisters.parallelStream().filter(eventRegister -> eventRegister.pluginEvent.equals(pluginEvent)).collect(Collectors.toList()).forEach(eventRegister -> {
            eventRegisters.remove(eventRegister);
            Commands.removeCommand(eventRegister);
        });
    }

    @Override
    public void handle() {
        parseLine(message);

        Runnable runnable = () -> ircManager.getPluginEvents().forEach((event) -> {
            try {
                PrivMsgEvent privMsgEvent = new PrivMsgEvent(ircManager, user, channel, event);
                privMsgEvent.setMessage(message);
                privMsgEvent.setArgs(new GapList<>(args));
                event.onPrivMsg(privMsgEvent);
            } catch (Exception e) {
                log.error("An error happened!", e);
            }
        });
        ircManager.getExecutor().execute(runnable);

        runnable = () -> invoke(eventRegisters, ircManager.getConfiguration().getPrefix());
        ircManager.getExecutor().execute(runnable);
    }

    private void invoke(List<EventRegister> eventRegisters, String prefix) {
        if (args.size() >= 1) {
            if (args.get(0).startsWith(prefix)) {
                args.set(0, args.get(0).substring(prefix.length()));

                String command = null;
                EventRegister eventRegister = null;

                for (EventRegister eventRegister1 : eventRegisters) {
                    if (args.get(0).equals(eventRegister1.command)) {
                        eventRegister = eventRegister1;
                        command = eventRegister1.command;
                        break;
                    } else {
                        for (String optCommand : eventRegister1.optCommands) {
                            if (args.get(0).equals(optCommand)) {
                                eventRegister = eventRegister1;
                                command = optCommand;
                                break;
                            }
                        }
                    }
                }

                if (eventRegister != null && command.length() != 0) {
                    args.remove(0);

                    String messageWithoutCommand =
                            message.substring(message.substring(1).length() > command.length()
                                    ? command.length() + 2
                                    : message.length());

                    OptionParser parser = new OptionParser();
                    parser.allowsUnrecognizedOptions();

                    for (String arg : eventRegister.args) {
                        if (arg.endsWith("::")) {
                            parser.accepts(arg.substring(0, arg.length() - 2)).withOptionalArg();
                        } else if (arg.endsWith(":")) {
                            parser.accepts(arg.substring(0, arg.length() - 1)).withRequiredArg();
                        } else {
                            parser.accepts(arg);
                        }
                    }

                    OptionSet optionSet = parser.parse(args.toArray(new String[args.size()]));
                    CommandEvent commandEvent = new CommandEvent(ircManager, user, channel, eventRegister.pluginEvent);
                    commandEvent.setMessage(messageWithoutCommand);
                    commandEvent.setArgs(args);
                    commandEvent.setCommand(command);
                    commandEvent.setOptionSet(optionSet);
                    commandEvent.setRawMessage(message);

                    Level level = Source.loopMask(ircManager, user.getCompleteRawLine());

                    switch (eventRegister.level) {
                        case NORMAL:
                            invoke(eventRegister.method, eventRegister.pluginEvent, commandEvent);
                            break;
                        case MOD:
                            if (level.equals(Level.MOD)
                                    || level.equals(Level.ADMIN)
                                    || level.equals(Level.OWNER)) {
                                invoke(eventRegister.method, eventRegister.pluginEvent, commandEvent);
                            }
                            break;
                        case ADMIN:
                            if (level.equals(Level.ADMIN)
                                    || level.equals(Level.OWNER)) {
                                invoke(eventRegister.method, eventRegister.pluginEvent, commandEvent);
                            }
                            break;
                        case OWNER:
                            if (level.equals(Level.OWNER)) {
                                invoke(eventRegister.method, eventRegister.pluginEvent, commandEvent);
                            }
                            break;
                    }
                }
            }
        }
    }

    private void invoke(Method method, PluginEvent pluginEvent, CommandEvent commandEvent) {
        try {
            method.invoke(pluginEvent, commandEvent);
        } catch (Exception e) {
            log.error("An error happened when invoking!", e);
        }
    }

    private void parseLine(String message) {
        Matcher matcher = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'").matcher(message);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Add double-quoted string without the quotes
                args.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                // Add single-quoted string without the quotes
                args.add(matcher.group(2));
            } else {
                // Add unquoted word
                args.add(matcher.group());
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class EventRegister {
        public final String command;
        public final String[] optCommands;
        public final String[] args;
        public final Method method;
        public final PluginEvent pluginEvent;
        public final Level level;
    }
}
