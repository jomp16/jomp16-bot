package tk.jomp16.plugin.utils.whoami;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;

public class Whoami extends Event {
    @Command("whoami")
    public void whoami(CommandListener commandListener) {
        commandListener.respond("You're: " + commandListener.getUser().getNickName() + ", and your level is: " + commandListener.getUser().getLevel().toString().toLowerCase());
    }
}
