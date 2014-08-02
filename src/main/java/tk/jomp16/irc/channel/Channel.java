package tk.jomp16.irc.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
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
            return ChannelList.getListUsers(targetName);
        }

        return null;
    }

    public List<User> getAllUsers() {
        if (targetName.startsWith("#")) {
            return new ArrayList<>(ChannelList.getListUsers(targetName).keySet());
        }

        return null;
    }

    public List<User> getAllOpUsers() {
        if (targetName.startsWith("#")) {
            List<User> opUsers = new ArrayList<>();

            ChannelList.getListUsers(targetName).forEach((user, level) -> {
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
            List<User> voiceUsers = new ArrayList<>();

            ChannelList.getListUsers(targetName).forEach((user, level) -> {
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
            List<User> normalUsers = new ArrayList<>();

            ChannelList.getListUsers(targetName).forEach((user, level) -> {
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
            return ChannelList.getChannelTopic(this.targetName);
        }

        return null;
    }

    public ChannelInfo getChannelInfo() {
        if (targetName.startsWith("#")) {
            return ChannelList.getChannelInfo(targetName);
        }

        return null;
    }
}
