/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser;

import lombok.Getter;
import lombok.ToString;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.plugin.level.Level;

@Getter
@ToString
public class Source {
    private String rawSource;
    private String nick;
    private String user;
    private String host;

    public Source(String rawSource) {
        this.rawSource = rawSource;

        if (!rawSource.contains("@")) {
            this.host = rawSource;
        } else {
            if (rawSource.contains("!")) {
                this.nick = rawSource.substring(0, rawSource.indexOf('!'));
                this.user = rawSource.substring(rawSource.indexOf('!') + 1, rawSource.indexOf('@'));
                this.host = rawSource.substring(rawSource.indexOf('@') + 1);
            } else {
                this.nick = rawSource.substring(0, rawSource.indexOf('@'));
                this.host = rawSource.substring(rawSource.indexOf('@') + 1);
            }
        }
    }

    public Source(String nick, String user, String host) {
        this.nick = nick;
        this.user = user;
        this.host = host;
        this.rawSource = nick + "!" + user + "@" + host;
    }

    public static boolean match(String host, String mask) {
        String[] sections = mask.split("\\*");

        for (String section : sections) {
            int index = host.indexOf(section);

            if (index == -1) {
                return false;
            }

            host = host.substring(index + section.length());
        }

        return true;
    }

    public static Level loopMask(IrcManager ircManager, String tmpMask) {
        for (String ownersMask : ircManager.getConfiguration().getOwners()) {
            if (Source.match(tmpMask, ownersMask)) {
                return Level.OWNER;
            }
        }

        for (String adminsMask : ircManager.getConfiguration().getAdmins()) {
            if (Source.match(tmpMask, adminsMask)) {
                return Level.ADMIN;
            }
        }

        for (String modsMask : ircManager.getConfiguration().getMods()) {
            if (Source.match(tmpMask, modsMask)) {
                return Level.MOD;
            }
        }

        return Level.NORMAL;
    }

    public boolean match(String mask) {
        return match(host, mask);
    }
}
