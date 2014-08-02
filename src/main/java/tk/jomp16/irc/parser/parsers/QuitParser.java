package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.QuitHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class QuitParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());

        UserList.removeUser(user);
        ChannelList.removeUserFromAllChannel(user);

        return new QuitHandler(ircManager, user, parserToken.getParams().size() >= 1 ? parserToken.getParams().get(0) : "");
    }
}
