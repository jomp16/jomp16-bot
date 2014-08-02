package tk.jomp16.irc.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tk.jomp16.irc.user.User;

@AllArgsConstructor
@Getter
@Setter
public class ChannelUser {
    private User user;
    private ChannelLevel channelLevel;
}
