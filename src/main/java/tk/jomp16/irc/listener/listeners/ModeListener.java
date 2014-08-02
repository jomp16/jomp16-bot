package tk.jomp16.irc.listener.listeners;

import lombok.Getter;
import lombok.Setter;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.listener.Listener;
import tk.jomp16.irc.modes.Mode;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.event.Event;

@Setter
@Getter
public class ModeListener extends Listener {
    private Mode mode;
    private String userModded;

    public ModeListener(IrcManager ircManager, User user, Channel channel, Event event, PluginInfo pluginInfo) {
        super(ircManager, user, channel, event, pluginInfo);
    }
}
