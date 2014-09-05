/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.PrivMsgHandler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;

public class PrivMsgParser implements Parser {
    private CtcpParser ctcpParser = new CtcpParser();

    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getSource().getNick().equals(ircManager.getConfiguration().getNick())) {
            return null;
        }

        User user = ircManager.getUserList().getUserFromHost(parserToken.getSource().getRawSource());

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
            return ctcpParser.parse(ircManager, parserToken);
        }

        return new PrivMsgHandler(ircManager, user, channel, message);
    }
}
