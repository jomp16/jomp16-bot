package tk.jomp16.plugin.help;

import org.apache.commons.lang3.StringUtils;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.level.Level;

import java.util.HashMap;
import java.util.Map;

public class Help extends Event {
    private static Map<String, String[]> helpNormal = new HashMap<>();
    private static Map<String, String[]> helpMod = new HashMap<>();
    private static Map<String, String[]> helpAdmin = new HashMap<>();
    private static Map<String, String[]> helpOwner = new HashMap<>();
    private static Map<String, HelpRegister> helpRegisters = new HashMap<>();
    private static LanguageManager languageManager;

    public static void addHelp(Event event) {
        event.getHelpRegister().parallelStream()
                .filter(helpRegister -> !helpRegisters.containsKey(helpRegister.getCommand()))
                .forEach(helpRegister -> {
                    helpRegisters.put(helpRegister.getCommand(), helpRegister);

                    switch (helpRegister.getLevel()) {
                        case NORMAL:
                            helpNormal.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            helpMod.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            helpAdmin.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            helpOwner.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            break;
                        case MOD:
                            helpMod.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            helpAdmin.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            helpOwner.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            break;
                        case ADMIN:
                            helpAdmin.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            helpOwner.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            break;
                        case OWNER:
                            helpOwner.put(helpRegister.getCommand(), helpRegister.getOptCommands());
                            break;
                    }
                });
    }

    public static void removeHelp(Event event) {
        event.getHelpRegister().parallelStream()
                .filter(helpRegister -> helpRegisters.containsKey(helpRegister.getCommand()))
                .forEach(helpRegister -> {
                    helpRegisters.remove(helpRegister.getCommand());

                    switch (helpRegister.getLevel()) {
                        case NORMAL:
                            helpNormal.remove(helpRegister.getCommand());
                            helpMod.remove(helpRegister.getCommand());
                            helpAdmin.remove(helpRegister.getCommand());
                            helpOwner.remove(helpRegister.getCommand());
                            break;
                        case MOD:
                            helpMod.remove(helpRegister.getCommand());
                            helpAdmin.remove(helpRegister.getCommand());
                            helpOwner.remove(helpRegister.getCommand());
                            break;
                        case ADMIN:
                            helpAdmin.remove(helpRegister.getCommand());
                            helpOwner.remove(helpRegister.getCommand());
                            break;
                        case OWNER:
                            helpOwner.remove(helpRegister.getCommand());
                            break;
                    }
                });
    }

    public static Map<String, String[]> getHelpNormal() {
        return helpNormal;
    }

    public static Map<String, String[]> getHelpMod() {
        return helpMod;
    }

    public static Map<String, String[]> getHelpAdmin() {
        return helpAdmin;
    }

    public static Map<String, String[]> getHelpOwner() {
        return helpOwner;
    }

    public static Map<String, HelpRegister> getHelpRegisters() {
        return helpRegisters;
    }

    @Command("help")
    public void help(CommandListener commandListener) throws Exception {
        if (commandListener.getArgs().size() > 0) {
            if (commandListener.getArgs().get(0).equals("all")) {
                commandListener.respond(languageManager.getAsString("help.text.available.commands", getAllHelp(commandListener.getUser().getLevel())));
            } else {
                final String[] tmp = {commandListener.getArgs().get(0)};

                if (helpRegisters.containsKey(tmp[0]) || helpRegisters.values().parallelStream().filter(register -> {
                    if (register.getOptCommands() != null) {
                        for (String command : register.getOptCommands()) {
                            if (command.equals(tmp[0])) {
                                tmp[0] = register.getCommand();

                                return true;
                            }
                        }
                    }

                    return false;
                }).count() != 0) {
                    HelpRegister helpRegister = helpRegisters.get(tmp[0]);
                    Level level = commandListener.getUser().getLevel();

                    switch (helpRegister.getLevel()) {
                        case NORMAL:
                            commandListener.respond(getHelpInfo(commandListener.getIrcManager(), helpRegister));
                            break;
                        case MOD:
                            if (level.equals(Level.MOD)) {
                                commandListener.respond(getHelpInfo(commandListener.getIrcManager(), helpRegister));
                            }
                            break;
                        case ADMIN:
                            if (level.equals(Level.ADMIN)) {
                                commandListener.respond(getHelpInfo(commandListener.getIrcManager(), helpRegister));
                            }
                            break;
                        case OWNER:
                            if (level.equals(Level.OWNER)) {
                                commandListener.respond(getHelpInfo(commandListener.getIrcManager(), helpRegister));
                            }
                            break;
                    }
                } else {
                    commandListener.respond(languageManager.getAsString("help.text.not.found"));
                }
            }
        } else {
            commandListener.showUsage("help");
        }
    }

    private String getAllHelp(Level level) {
        StringBuilder stringBuilder = new StringBuilder();

        switch (level) {
            case NORMAL:
                helpNormal.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
            case MOD:
                helpMod.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
            case ADMIN:
                helpAdmin.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
            case OWNER:
                helpOwner.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
        }

        String tmp = stringBuilder.toString();

        return tmp.substring(0, tmp.length() - 2);
    }

    private String getHelpInfo(IrcManager ircManager, HelpRegister helpRegister) {
        if (helpRegister.getOptCommands() != null && helpRegister.getOptCommands().length != 0) {
            String usage = helpRegister.getUsage();

            if (usage != null) {
                return languageManager.getAsString("help.text.with.opt.command",
                        ircManager.getConfiguration().getPrefix(),
                        helpRegister.getCommand(),
                        StringUtils.join(helpRegister.getOptCommands(), ", "),
                        helpRegister.getHelp(),
                        helpRegister.getUsage());
            } else {
                return languageManager.getAsString("help.text.with.opt.command",
                        ircManager.getConfiguration().getPrefix(),
                        helpRegister.getCommand(),
                        StringUtils.join(helpRegister.getOptCommands(), ", "),
                        helpRegister.getHelp(),
                        "");
            }
        } else {
            String usage = helpRegister.getUsage();

            if (usage != null) {
                return languageManager.getAsString("help.text.without.opt.command",
                        ircManager.getConfiguration().getPrefix(),
                        helpRegister.getCommand(),
                        helpRegister.getHelp(),
                        helpRegister.getUsage());
            } else {
                return languageManager.getAsString("help.text.without.opt.command",
                        ircManager.getConfiguration().getPrefix(),
                        helpRegister.getCommand(),
                        helpRegister.getHelp(),
                        "");
            }
        }
    }

    @Override
    public void onInit(InitListener initListener) throws Exception {
        languageManager = new LanguageManager("lang.Plugins");

        initListener.addHelp(new HelpRegister("help", languageManager.getAsString("help.help.text"), languageManager.getAsString("help.help.usage")));
    }
}
