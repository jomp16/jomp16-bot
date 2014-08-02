package tk.jomp16.plugin.utils.ram;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.utils.Utils;

public class Ram extends Event {
    @Command("ram")
    public void ram(CommandListener commandListener) {
        commandListener.respond(Utils.getRamUsage());
    }
}
