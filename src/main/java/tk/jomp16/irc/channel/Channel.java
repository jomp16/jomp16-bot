/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.magicwerk.brownies.collections.GapList;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.user.User;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@ToString
public class Channel {
    private final IrcManager ircManager;
    private final String targetName;

    public ChannelActions getChannelActions() {
        if (targetName.startsWith("#")) {
            return new ChannelActions(ircManager, this);
        }

        return null;
    }

    public Map<User, ChannelLevel> getAllUsersWithLevel() {
        if (targetName.startsWith("#")) {
            return ircManager.getChannelList().getListUsers(targetName);
        }

        return null;
    }

    public List<User> getAllUsers() {
        if (targetName.startsWith("#")) {
            return new GapList<>(ircManager.getChannelList().getListUsers(targetName).keySet());
        }

        return null;
    }

    public List<User> getAllOpUsers() {
        if (targetName.startsWith("#")) {
            List<User> opUsers = new GapList<>();

            ircManager.getChannelList().getListUsers(targetName).forEach((user, level) -> {
                if (level.equals(ChannelLevel.OP)) {
                    opUsers.add(user);
                }
            });

            return opUsers;
        }

        return null;
    }

    public List<User> getAllVoiceUsers() {
        if (targetName.startsWith("#")) {
            List<User> voiceUsers = new GapList<>();

            ircManager.getChannelList().getListUsers(targetName).forEach((user, level) -> {
                if (level.equals(ChannelLevel.VOICE)) {
                    voiceUsers.add(user);
                }
            });

            return voiceUsers;
        }

        return null;
    }

    public List<User> getAllNormalUsers() {
        if (targetName.startsWith("#")) {
            List<User> normalUsers = new GapList<>();

            ircManager.getChannelList().getListUsers(targetName).forEach((user, level) -> {
                if (level.equals(ChannelLevel.NORMAL)) {
                    normalUsers.add(user);
                }
            });

            return normalUsers;
        }

        return null;
    }

    public String getChannelTopic() {
        if (targetName.startsWith("#")) {
            return ircManager.getChannelList().getChannelTopic(this.targetName);
        }

        return null;
    }

    public ChannelInfo getChannelInfo() {
        if (targetName.startsWith("#")) {
            return ircManager.getChannelList().getChannelInfo(targetName);
        }

        return null;
    }
}
