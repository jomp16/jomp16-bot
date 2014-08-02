package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.channel.ChannelLevel;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.JoinHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class JoinParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getSource().getNick().equals(ircManager.getConfiguration().getNick())) {
            return null;
        }

        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());

        Channel channel;

        if (parserToken.getParams().get(0).startsWith("#")) {
            channel = new Channel(ircManager, parserToken.getParams().get(0));
        } else {
            return null;
        }

        ChannelList.addUserToChannel(channel.getTargetName(), user, ChannelLevel.NORMAL);

        return new JoinHandler(ircManager, user, channel);
    }
}
