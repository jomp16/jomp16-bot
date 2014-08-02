package tk.jomp16.irc.listener.listeners;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.listener.Listener;
import tk.jomp16.irc.user.User;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.event.Event;

@Log4j2
public class InitListener extends Listener {
    public InitListener(IrcManager ircManager, User user, Channel channel, Event event, PluginInfo pluginInfo) {
        super(ircManager, user, channel, event, pluginInfo);
    }
}
