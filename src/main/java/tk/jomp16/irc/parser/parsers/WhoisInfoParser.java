package tk.jomp16.irc.parser.parsers;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.IrcParser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.UserList;

@Log4j2
public class WhoisInfoParser extends IrcParser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getCommand().equals("311")) {
            String userNick = parserToken.getParams().get(1);
            String userHost = userNick + "!" + parserToken.getParams().get(2) + "@" + parserToken.getParams().get(3);
            String realName = parserToken.getParams().get(5);

            UserList.setHost(userNick, userHost);
            UserList.setRealName(userNick, realName);
        }

        return null;
    }
}
