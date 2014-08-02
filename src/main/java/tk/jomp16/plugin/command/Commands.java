package tk.jomp16.plugin.command;

import org.apache.commons.lang3.StringUtils;
import tk.jomp16.irc.handler.handlers.PrivMsgHandler;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.level.Level;

import java.util.HashMap;
import java.util.Map;

public class Commands extends Event {
    private static Map<String, String[]> commandsNormal = new HashMap<>();
    private static Map<String, String[]> commandsMod = new HashMap<>();
    private static Map<String, String[]> commandsAdmin = new HashMap<>();
    private static Map<String, String[]> commandsOwner = new HashMap<>();
    private LanguageManager languageManager;

    public static void removeCommand(PrivMsgHandler.EventRegister register) {
        switch (register.level) {
            case NORMAL:
                commandsNormal.remove(register.command);
                commandsMod.remove(register.command);
                commandsAdmin.remove(register.command);
                commandsOwner.remove(register.command);
                break;
            case MOD:
                commandsMod.remove(register.command);
                commandsAdmin.remove(register.command);
                commandsOwner.remove(register.command);
                break;
            case ADMIN:
                commandsAdmin.remove(register.command);
                commandsOwner.remove(register.command);
                break;
            case OWNER:
                commandsOwner.remove(register.command);
                break;
        }
    }

    public static void addCommand(PrivMsgHandler.EventRegister register) {
        switch (register.level) {
            case NORMAL:
                commandsNormal.put(register.command, register.optCommands);
                commandsMod.put(register.command, register.optCommands);
                commandsAdmin.put(register.command, register.optCommands);
                commandsOwner.put(register.command, register.optCommands);
                break;
            case MOD:
                commandsMod.put(register.command, register.optCommands);
                commandsAdmin.put(register.command, register.optCommands);
                commandsOwner.put(register.command, register.optCommands);
                break;
            case ADMIN:
                commandsAdmin.put(register.command, register.optCommands);
                commandsOwner.put(register.command, register.optCommands);
                break;
            case OWNER:
                commandsOwner.put(register.command, register.optCommands);
                break;
        }
    }

    @Command("commands")
    public void commands(CommandListener commandListener) {
        commandListener.respond(languageManager.getAsString("commands.text.available.commands", getAvailableCommands(commandListener.getUser().getLevel())));
    }

    private String getAvailableCommands(Level level) {
        StringBuilder stringBuilder = new StringBuilder();

        switch (level) {
            case NORMAL:
                commandsNormal.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null && !value[0].equals("")) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
            case MOD:
                commandsMod.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null && !value[0].equals("")) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
            case ADMIN:
                commandsAdmin.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null && !value[0].equals("")) {
                        stringBuilder.append(" [")
                                .append(StringUtils.join(value, ", "))
                                .append("]");
                    }

                    stringBuilder.append(", ");
                });

                break;
            case OWNER:
                commandsOwner.forEach((key, value) -> {
                    stringBuilder.append(key);

                    if (value != null && !value[0].equals("")) {
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

    @Override
    public void onInit(InitListener initListener) throws Exception {
        languageManager = new LanguageManager("lang.Plugins");

//        initListener.addHelp(this, new HelpRegister("commands", languageManager.getAsString("commands.help.text")));
    }
}
