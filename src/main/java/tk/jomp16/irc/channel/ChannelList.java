package tk.jomp16.irc.channel;

import com.google.common.collect.HashMultimap;
import lombok.Synchronized;
import tk.jomp16.irc.modes.Mode;
import tk.jomp16.irc.user.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChannelList {
    private static HashMultimap<String, ChannelUser> hashMapChannelUsers = HashMultimap.create();
    private static Map<String, ChannelInfo> channelInfo = new HashMap<>();

    @Synchronized
    public static void setTopic(String channel, String topic) {
        if (channelInfo.containsKey(channel)) {
            channelInfo.get(channel).setTopic(topic);
        } else {
            channelInfo.put(channel, new ChannelInfo(topic, null, null));
        }
    }

    @Synchronized
    public static void setBy(String channel, String setByUser, LocalDateTime localDateTime) {
        if (channelInfo.containsKey(channel)) {
            channelInfo.get(channel).setTopicSetBy(setByUser);
            channelInfo.get(channel).setTopicSetAt(localDateTime);
        } else {
            channelInfo.put(channel, new ChannelInfo(null, setByUser, localDateTime));
        }
    }

    @Synchronized
    public static String getChannelTopic(String channel) {
        if (channelInfo.containsKey(channel)) {
            return channelInfo.get(channel).getTopic();
        }

        return null;
    }

    @Synchronized
    public static ChannelInfo getChannelInfo(String channel) {
        if (channelInfo.containsKey(channel)) {
            return channelInfo.get(channel);
        }

        return null;
    }

    @Synchronized
    public static void addUserToChannel(String channel, User user, ChannelLevel level) {
        ChannelUser channelUser = new ChannelUser(user, level);

        hashMapChannelUsers.put(channel, channelUser);
    }

    @Synchronized
    public static void removeUserFromChannel(String channel, User user) {
        if (hashMapChannelUsers.containsKey(channel)) {
            for (Iterator<ChannelUser> iterator = hashMapChannelUsers.get(channel).iterator(); iterator.hasNext(); ) {
                ChannelUser channelUser = iterator.next();

                if (channelUser.getUser().equals(user)) {
                    iterator.remove();

                    break;
                }
            }
        }
    }

    @Synchronized
    public static void removeUserFromAllChannel(User user) {
        for (Iterator<ChannelUser> iterator = hashMapChannelUsers.values().iterator(); iterator.hasNext(); ) {
            ChannelUser channelUser = iterator.next();

            if (channelUser.getUser().equals(user)) {
                iterator.remove();
            }
        }
    }

    @Synchronized
    public static void changeNick(String oldNick, String newNick) {
        HashMultimap<String, ChannelUser> tmp = HashMultimap.create();

        for (String s : hashMapChannelUsers.keySet()) {
            for (ChannelUser channelUser : hashMapChannelUsers.get(s)) {
                if (channelUser.getUser().getNickName().equals(oldNick)) {
                    channelUser.getUser().setNickName(newNick);
                }

                tmp.put(s, channelUser);
            }
        }

        hashMapChannelUsers.clear();
        hashMapChannelUsers.putAll(tmp);
    }

    @Synchronized
    public static void changeUserLevel(String channel, String nick, Mode mode) {
        if (hashMapChannelUsers.containsKey(channel)) {
            ChannelUser channelUser1 = null;

            for (Iterator<ChannelUser> iterator = hashMapChannelUsers.get(channel).iterator(); iterator.hasNext(); ) {
                ChannelUser channelUser = iterator.next();

                if (channelUser.getUser().getNickName().equals(nick)) {
                    ChannelLevel level = ChannelLevel.NORMAL;

                    switch (mode) {
                        case VOICE:
                            level = ChannelLevel.VOICE;
                            break;
                        case OP:
                            level = ChannelLevel.OP;
                            break;
                    }

                    channelUser.setChannelLevel(level);
                    channelUser1 = channelUser;

                    iterator.remove();

                    break;
                }
            }

            if (channelUser1 != null) {
                hashMapChannelUsers.put(channel, channelUser1);
            }
        }
    }

    @Synchronized
    public static Map<User, ChannelLevel> getListUsers(String channel) {
        Map<User, ChannelLevel> tmp = new HashMap<>();

        hashMapChannelUsers.entries().stream().filter(entry -> entry.getKey().equals(channel)).forEach(entry -> {
            ChannelUser channelUser = entry.getValue();
            tmp.put(channelUser.getUser(), channelUser.getChannelLevel());
        });

        return tmp;
    }
}
