package tk.jomp16.irc.channel;

import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.user.User;

@RequiredArgsConstructor
public class ChannelActions {
    private final IrcManager ircManager;
    private final Channel channel;

    public synchronized void partChannel() {
        ircManager.getOutputIrc().partChannel(channel.getTargetName());
    }

    public synchronized void partChannel(String reason) {
        ircManager.getOutputIrc().partChannel(channel.getTargetName(), reason);
    }

    public synchronized void giveOP(User user) {
        ircManager.getOutputIrc().giveOP(channel.getTargetName(), user.getNickName());
    }

    public synchronized void giveVoice(User user) {
        ircManager.getOutputIrc().giveVoice(channel.getTargetName(), user.getNickName());
    }

    public synchronized void removeOP(User user) {
        ircManager.getOutputIrc().removeOP(channel.getTargetName(), user.getNickName());
    }

    public synchronized void removeVoice(User user) {
        ircManager.getOutputIrc().removeVoice(channel.getTargetName(), user.getNickName());
    }
}
