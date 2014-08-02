package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.PartHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class PartParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());
        Channel channel = new Channel(ircManager, parserToken.getParams().get(0));
        String reason = "";

        if (parserToken.getParams().size() >= 2) {
            reason = parserToken.getParams().get(1);
        }

        UserList.removeUser(user);
        ChannelList.removeUserFromChannel(channel.getTargetName(), user);

        return new PartHandler(ircManager, user, channel, reason);
    }
}
