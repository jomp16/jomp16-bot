package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.KickHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class KickParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());
        Channel channel = new Channel(ircManager, parserToken.getParams().get(0));

        String userKicked = parserToken.getParams().get(1);
        String reason = parserToken.getParams().get(2);

        ChannelList.removeUserFromChannel(channel.getTargetName(), UserList.getUserFromNick(userKicked));

        return new KickHandler(ircManager, user, channel, UserList.getUserFromNick(userKicked), reason);
    }
}
