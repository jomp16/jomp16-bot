package tk.jomp16.irc.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;

@RequiredArgsConstructor
public class OutputIrc {
    private final IrcManager ircManager;

    public void joinChannel(@NonNull Object channel) {
        ircManager.getOutputRaw().writeRaw("JOIN " + channel);
    }

    public void joinChannel(@NonNull Object channel, @NonNull Object password) {
        ircManager.getOutputRaw().writeRaw("JOIN " + channel + " " + password);
    }

    public void partChannel(@NonNull Object channel) {
        ircManager.getOutputRaw().writeRaw("PART " + channel);
    }

    public void partChannel(@NonNull Object channel, @NonNull Object reason) {
        ircManager.getOutputRaw().writeRaw("PART " + channel + " :" + reason);
    }

    public void giveOP(@NonNull Object channel, @NonNull Object user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " +o " + user);
    }

    public void giveVoice(@NonNull Object channel, @NonNull Object user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " +v " + user);
    }

    public void removeOP(@NonNull Object channel, @NonNull Object user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " -o " + user);
    }

    public void removeVoice(@NonNull Object channel, @NonNull Object user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " -v " + user);
    }

    public void sendPrivMsg(@NonNull Object target, @NonNull Object message) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :" + message);
    }

    public void sendPrivMsg(@NonNull Object target, @NonNull Object username, @NonNull Object message) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :" + username + ": " + message);
    }

    public void sendNotice(@NonNull Object target, @NonNull Object message) {
        ircManager.getOutputRaw().writeRaw("NOTICE " + target + " :" + message);
    }

    public void changeNick(String nick) {
        ircManager.getConfiguration().setNick(nick);
        ircManager.getOutputRaw().writeRaw("NICK " + nick);
    }

    public void sendWhois(Object user) {
        ircManager.getOutputRaw().writeRaw("WHOIS " + user);
    }

    public void quit() {
        ircManager.getOutputRaw().writeRaw("QUIT");
    }

    public void quit(String reason) {
        ircManager.getOutputRaw().writeRaw("QUIT " + reason);
    }
}
