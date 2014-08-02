package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.PrivMsgHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class PrivMsgParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getSource().getNick().equals(ircManager.getConfiguration().getNick())) {
            return null;
        }

        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());

        Channel channel;

        if (parserToken.getParams().get(0).startsWith("#")) {
            channel = new Channel(ircManager, parserToken.getParams().remove(0));
        } else {
            channel = new Channel(ircManager, user.getNickName());
            parserToken.getParams().remove(0);
        }

        String message = parserToken.getParams().get(0);

        if (message.startsWith("\001")) {
            // todo: CTCP
        }

        return new PrivMsgHandler(user, channel, message);
    }
}
