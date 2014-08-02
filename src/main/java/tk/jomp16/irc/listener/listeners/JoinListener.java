package tk.jomp16.irc.listener.listeners;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.listener.Listener;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.event.Event;

public class JoinListener extends Listener {
    public JoinListener(IrcManager ircManager, User user, Channel channel, Event event, PluginInfo pluginInfo) {
        super(ircManager, user, channel, event, pluginInfo);
    }
}
