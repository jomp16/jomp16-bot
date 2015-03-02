/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser.parsers;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;

@Log4j2
public class WhoisInfoParser implements Parser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getCommand().equals("311")) {
            String userNick = parserToken.getParams().get(1);
            String userHost = userNick + "!" + parserToken.getParams().get(2) + "@" + parserToken.getParams().get(3);
            String realName = parserToken.getParams().get(5);

            ircManager.getUserList().setHost(userNick, userHost);
            ircManager.getUserList().setRealName(userNick, realName);
        }

        return null;
    }
}
