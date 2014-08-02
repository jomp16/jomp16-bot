package tk.jomp16.irc.handler.handlers;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.PrivMsgListener;
import tk.jomp16.irc.parser.Source;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.command.Commands;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.level.Level;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Log4j2
public class PrivMsgHandler implements Handler {
    @Getter
    @Setter
    private static IrcManager ircManager;
    private static List<EventRegister> eventRegisters = new ArrayList<>();
    private final User user;
    private final Channel channel;
    private final String message;
    private List<String> args = new ArrayList<>();

    public static void addCommandsFromEvent(Event event) {
        Method[] methods = event.getClass().getDeclaredMethods();

        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(Command.class);
            if (annotation != null) {
                method.setAccessible(true);
                Command command = (Command) annotation;
                PluginInfo pluginInfo = ircManager.getPluginInfoHashMap().get(event.getClass().getSimpleName());
                EventRegister eventRegister = new EventRegister(command.value(), command.optCommands(), command.args(), method, event, command.level(), pluginInfo);
                eventRegisters.add(eventRegister);
                Commands.addCommand(eventRegister);
            }
        }
    }

    public static void removeCommandsFromEventName(Event event) {
        eventRegisters.parallelStream().filter(eventRegister -> eventRegister.event.equals(event)).collect(Collectors.toList()).forEach(eventRegister -> {
            eventRegisters.remove(eventRegister);
            Commands.removeCommand(eventRegister);
        });
    }

    @Override
    public void respond() {
        parseLine(message);

        Runnable runnable = () -> ircManager.getEvents().forEach((event) -> {
            try {
                PrivMsgListener privMsgListener = new PrivMsgListener(ircManager, user, channel, event, ircManager.getPluginInfoFromEvent(event));
                privMsgListener.setMessage(message);
                privMsgListener.setArgs(new ArrayList<>(args));
                event.onPrivMsg(privMsgListener);
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
                    CommandListener commandListener = new CommandListener(ircManager, user, channel, eventRegister.event, eventRegister.pluginInfo);
                    commandListener.setMessage(messageWithoutCommand);
                    commandListener.setArgs(args);
                    commandListener.setCommand(command);
                    commandListener.setOptionSet(optionSet);
                    commandListener.setRawMessage(message);

                    Level level = Source.loopMask(ircManager, user.getCompleteRawLine());

                    switch (eventRegister.level) {
                        case NORMAL:
                            invoke(eventRegister.method, eventRegister.event, commandListener);
                            break;
                        case MOD:
                            if (level.equals(Level.MOD)
                                    || level.equals(Level.ADMIN)
                                    || level.equals(Level.OWNER)) {
                                invoke(eventRegister.method, eventRegister.event, commandListener);
                            }
                            break;
                        case ADMIN:
                            if (level.equals(Level.ADMIN)
                                    || level.equals(Level.OWNER)) {
                                invoke(eventRegister.method, eventRegister.event, commandListener);
                            }
                            break;
                        case OWNER:
                            if (level.equals(Level.OWNER)) {
                                invoke(eventRegister.method, eventRegister.event, commandListener);
                            }
                            break;
                    }
                }
            }
        }
    }

    private void invoke(Method method, Event event, CommandListener commandListener) {
        try {
            method.invoke(event, commandListener);
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
        public final Event event;
        public final Level level;
        public final PluginInfo pluginInfo;
    }
}
