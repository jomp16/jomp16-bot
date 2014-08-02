package tk.jomp16.bot;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.configuration.Configuration;
import tk.jomp16.irc.IrcManager;

import java.io.FileReader;

@Log4j2
public class BotMain {
    private static IrcManager ircManager;

    public static void main(String[] args) throws Exception {
        log.info("Welcome to jomp16-bot!");

        ircManager = new IrcManager(new Gson().fromJson(new FileReader("config.json"), Configuration.class));
        ircManager.startIrc();
    }
}