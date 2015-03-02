/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;

@RequiredArgsConstructor
public class OutputIrc {
    private final IrcManager ircManager;

    public void joinChannel(@NonNull String channel) {
        ircManager.getOutputRaw().writeRaw("JOIN " + channel);
    }

    public void joinChannel(@NonNull String channel, @NonNull String password) {
        ircManager.getOutputRaw().writeRaw("JOIN " + channel + " " + password);
    }

    public void partChannel(@NonNull String channel) {
        ircManager.getOutputRaw().writeRaw("PART " + channel);
    }

    public void partChannel(@NonNull String channel, @NonNull String reason) {
        ircManager.getOutputRaw().writeRaw("PART " + channel + " :" + reason);
    }

    public void giveOP(@NonNull String channel, @NonNull String user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " +o " + user);
    }

    public void giveVoice(@NonNull String channel, @NonNull String user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " +v " + user);
    }

    public void removeOP(@NonNull String channel, @NonNull String user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " -o " + user);
    }

    public void removeVoice(@NonNull String channel, @NonNull String user) {
        ircManager.getOutputRaw().writeRaw("MODE " + channel + " -v " + user);
    }

    public void sendPrivMsg(@NonNull String target, @NonNull String message) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :", message);
    }

    public void sendPrivMsg(@NonNull String target, @NonNull String username, @NonNull String message) {
        ircManager.getOutputRaw().writeRaw("PRIVMSG " + target + " :" + username + ": ", message);
    }

    public void sendNotice(@NonNull String target, @NonNull String message) {
        ircManager.getOutputRaw().writeRaw("NOTICE " + target + " :", message);
    }

    public void changeNick(String nick) {
        ircManager.getConfiguration().setNick(nick);
        ircManager.getOutputRaw().writeRaw("NICK " + nick);
    }

    public void sendWhois(String user) {
        ircManager.getOutputRaw().writeRaw("WHOIS " + user);
    }

    public void quit() {
        ircManager.getOutputRaw().writeRaw("QUIT");
    }

    public void quit(String reason) {
        ircManager.getOutputRaw().writeRaw("QUIT " + reason);
    }
}
