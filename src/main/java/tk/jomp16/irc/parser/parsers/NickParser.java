package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.NickHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class NickParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());

        String oldNick = parserToken.getSource().getNick();
        String newNick = parserToken.getParams().get(0);
        Channel channel = new Channel(ircManager, parserToken.getParams().get(0));

        UserList.changeNickFromHost(parserToken.getSource().getRawSource(), newNick);

        return new NickHandler(ircManager, user, channel, oldNick, newNick);
    }
}
