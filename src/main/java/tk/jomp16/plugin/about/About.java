package tk.jomp16.plugin.about;

import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.help.HelpRegister;

public class About extends Event {
    private LanguageManager languageManager;

    @Command("about")
    public void about(CommandListener commandListener) {
        commandListener.respond(languageManager.getAsString("about.text.about"));
    }

    @Override
    public void onInit(InitListener initListener) throws Exception {
        languageManager = new LanguageManager("lang.Plugins");

        initListener.addHelp(new HelpRegister("about", languageManager.getAsString("about.help.text")));
    }
}
