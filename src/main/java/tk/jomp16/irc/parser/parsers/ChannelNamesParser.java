package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.ChannelLevel;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.UserList;

public class ChannelNamesParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        // todo: support literally all IRC modes
        // todo: add a way to add a User object into those nicks
        String channel = parserToken.getParams().get(2);
        String[] users = parserToken.getParams().get(3).split(" ");

        boolean removeFirst = false;

        for (String user : users) {
            switch (user.charAt(0)) {
                case '@':
                    removeFirst = true;
                    ChannelList.addUserToChannel(channel, UserList.getUserFromNick(user.substring(1)), ChannelLevel.OP);
                    break;
                case '+':
                    removeFirst = true;
                    ChannelList.addUserToChannel(channel, UserList.getUserFromNick(user.substring(1)), ChannelLevel.VOICE);
                    break;
                default:
                    ChannelList.addUserToChannel(channel, UserList.getUserFromNick(user), ChannelLevel.NORMAL);
                    break;
            }

            ircManager.getOutputIrc().sendWhois(removeFirst ? user.substring(1) : user);
        }

        return null;
    }
}
