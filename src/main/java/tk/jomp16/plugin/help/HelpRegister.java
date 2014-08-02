package tk.jomp16.plugin.help;

import lombok.Getter;
import tk.jomp16.plugin.level.Level;

@Getter
public class HelpRegister {
    private String command;
    private String[] optCommands;
    private String help;
    private String usage;
    private Level level;

    /**
     * Help class for register a help
     *
     * @param command the command
     * @param help    the description/help of the command
     * @param usage   the usage of the command, like (command doSomething "Hello World!")
     */
    public HelpRegister(String command, String[] optCommands, String help, String usage) {
        this.command = command;
        this.optCommands = optCommands;
        this.help = help;
        this.usage = usage;
        this.level = Level.NORMAL;
    }

    public HelpRegister(String command, String help, String usage) {
        this.command = command;
        this.help = help;
        this.usage = usage;
        this.level = Level.NORMAL;
    }

    public HelpRegister(String command, String[] optCommands, String help) {
        this.command = command;
        this.optCommands = optCommands;
        this.help = help;
        this.level = Level.NORMAL;
    }

    public HelpRegister(String command, String help) {
        this.command = command;
        this.help = help;
        this.level = Level.NORMAL;
    }

    /**
     * Help class for register a help
     *
     * @param command the command
     * @param help    the description/help of the command
     * @param usage   the usage of the command, like (command doSomething "Hello World!")
     * @param level   the level who the command need for level greater than NORMAL
     */
    public HelpRegister(String command, String[] optCommands, String help, String usage, Level level) {
        this.command = command;
        this.optCommands = optCommands;
        this.help = help;
        this.usage = usage;
        this.level = level;
    }

    public HelpRegister(String command, String help, String usage, Level level) {
        this.command = command;
        this.help = help;
        this.usage = usage;
        this.level = level;
    }

    public HelpRegister(String command, String[] optCommands, String help, Level level) {
        this.command = command;
        this.optCommands = optCommands;
        this.help = help;
        this.level = level;
    }

    public HelpRegister(String command, String help, Level level) {
        this.command = command;
        this.help = help;
        this.level = level;
    }
}
