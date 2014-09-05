/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.QuitHandler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;

public class QuitParser implements Parser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        User user = ircManager.getUserList().getUserFromHost(parserToken.getSource().getRawSource());

        ircManager.getUserList().removeUser(user);
        ircManager.getChannelList().removeUserFromAllChannel(user);

        return new QuitHandler(ircManager, user, parserToken.getParams().size() >= 1 ? parserToken.getParams().get(0) : "");
    }
}
