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
import tk.jomp16.irc.handler.handlers.PartHandler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;

public class PartParser implements Parser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        User user = ircManager.getUserList().getUserFromHost(parserToken.getSource().getRawSource());
        Channel channel = new Channel(ircManager, parserToken.getParams().get(0));
        String reason = "";

        if (parserToken.getParams().size() >= 2) {
            reason = parserToken.getParams().get(1);
        }

//        ircManager.getUserList().removeUser(user);
        ircManager.getChannelList().removeUserFromChannel(channel.getTargetName(), user);

        return new PartHandler(ircManager, user, channel, reason);
    }
}
