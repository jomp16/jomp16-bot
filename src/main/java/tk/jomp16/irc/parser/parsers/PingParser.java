package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.PingHandler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;

public class PingParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        return new PingHandler(ircManager, parserToken.getParams().get(0));
    }
}
