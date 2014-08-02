package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;

public class NoticeParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getRawLine().contains("/msg NickServ identify")) {
            ircManager.getOutputIrc().sendPrivMsg("NickServ", "identify " + ircManager.getConfiguration().getPassword());
        }

        return null;
    }
}
