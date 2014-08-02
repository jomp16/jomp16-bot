package tk.jomp16.plugin.utils.channel;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.level.Level;

public class Channel extends Event {
    @Command(value = "join", level = Level.OWNER)
    public void join(CommandListener commandListener) {
        if (commandListener.getArgs().size() > 0) {
            commandListener.getIrcManager().getOutputIrc().joinChannel(commandListener.getArgs().get(0));
            commandListener.respond("Done!");
        }
    }

    @Command(value = "part", level = Level.OWNER)
    public void part(CommandListener commandListener) {
        if (commandListener.getArgs().size() > 0) {
            commandListener.getIrcManager().getOutputIrc().partChannel(commandListener.getArgs().get(0));
            commandListener.respond("Done!");
        }
    }
}