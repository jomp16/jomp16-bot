package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.channel.ChannelList;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.ModeHandler;
import tk.jomp16.irc.modes.Mode;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;
import tk.jomp16.irc.user.UserList;

public class ModeParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getRawLine().contains(ircManager.getConfiguration().getNick() + " :+i")) {
            ircManager.joinAllChannels();
            return null;
        }

        /*if (parserToken.getSource().getNick().equals(ircManager.getConfiguration().getNick())) {
            return null;
        }*/

        User user = UserList.getUserFromHost(parserToken.getSource().getRawSource());

        Channel channel = new Channel(ircManager, parserToken.getParams().get(0));
        Mode mode = Mode.getMode(parserToken.getParams().get(1));
        String userModded = parserToken.getParams().get(2);

        ChannelList.changeUserLevel(channel.getTargetName(), userModded, mode);

        return new ModeHandler(ircManager, user, channel, mode, userModded);
    }
}
