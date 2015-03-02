/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.user;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.parser.Source;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class UserList {
    private final IrcManager ircManager;
    private Map<String, User> userMap = new HashMap<>();

    @Synchronized
    public User getUserFromNick(String nick) {
        if (userMap.containsKey(nick)) {
            return userMap.get(nick);
        } else {
            User user = new User(nick, null, null, null, null);

            userMap.put(nick, user);

            return user;
        }
    }

    @Synchronized
    public void changeNickFromNick(String oldNick, String newNick) {
        User user = getUserFromNick(oldNick);

        user.setNickName(newNick);
    }

    @Synchronized
    public void changeNickFromHost(String host, String newNick) {
        User user = getUserFromHost(host);

        user.setNickName(newNick);
    }

    @Synchronized
    public void setHost(String userNick, String host) {
        Source source = new Source(host);

        User user = getUserFromNick(userNick);
        user.setHostName(source.getUser());
        user.setHostMask(source.getHost());
        user.setLevel(Source.loopMask(ircManager, source.getRawSource()));
    }

    @Synchronized
    public void setHost(String host) {
        Source source = new Source(host);

        User user = getUserFromNick(source.getNick());
        user.setHostName(source.getUser());
        user.setHostMask(source.getHost());
        user.setLevel(Source.loopMask(ircManager, source.getRawSource()));
    }

    @Synchronized
    public User getUserFromHost(String host) {
        for (User user : userMap.values()) {
            if (user.getCompleteRawLine().equals(host)) {
                return user;
            }
        }

        Source source = new Source(host);
        User user = new User(source.getNick(), source.getUser(), source.getHost(), null, Source.loopMask(ircManager, source.getRawSource()));

        userMap.put(source.getNick(), user);

        return user;
    }

    @Synchronized
    public void setRealName(String nick, String realName) {
        User user = getUserFromNick(nick);
        user.setRealName(realName);
    }

    public void removeUser(User user) {
        userMap.remove(user.getNickName());
    }
}
