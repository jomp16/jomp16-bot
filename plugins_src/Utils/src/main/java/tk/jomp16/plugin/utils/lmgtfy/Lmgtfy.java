package tk.jomp16.plugin.utils.lmgtfy;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;

import java.net.URLEncoder;

public class Lmgtfy extends Event {
    private String URL = "http://lmgtfy.com/?q=%s";

    @Command(value = "lmgtfy", args = {"user:", "term:"})
    public void lmgtfy(CommandListener commandListener) throws Exception {
        if (commandListener.getOptionSet().has("user") && commandListener.getOptionSet().has("term")) {
            String formatted = String.format(URL, URLEncoder.encode((String) commandListener.getOptionSet().valueOf("term"), "UTF-8"));

            commandListener.respond((String) commandListener.getOptionSet().valueOf("user"), formatted);
        } else {
            // todo: help
        }
    }

    @Override
    public void onInit(InitListener initListener) {
        // todo: help
    }
}
