/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.ChannelLevel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;

public class ChannelNamesParser implements Parser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        // todo: support literally all IRC modes
        // todo: add a way to add a User object into those nicks
        String channel = parserToken.getParams().get(2);
        String[] users = parserToken.getParams().get(3).split(" ");

        boolean removeFirst = false;

        for (String user : users) {
            if (removeFirst) {
                removeFirst = false;
            }

            switch (user.charAt(0)) {
                case '@':
                    removeFirst = true;
                    ircManager.getChannelList().addUserToChannel(channel, ircManager.getUserList().getUserFromNick(user.substring(1)), ChannelLevel.OP);
                    break;
                case '+':
                    removeFirst = true;
                    ircManager.getChannelList().addUserToChannel(channel, ircManager.getUserList().getUserFromNick(user.substring(1)), ChannelLevel.VOICE);
                    break;
                default:
                    ircManager.getChannelList().addUserToChannel(channel, ircManager.getUserList().getUserFromNick(user), ChannelLevel.NORMAL);
                    break;
            }

            String tmpUserNick = removeFirst ? user.substring(1) : user;

            if (ircManager.getUserList().getUserFromNick(tmpUserNick).getRealName() == null) {
                ircManager.getOutputIrc().sendWhois(removeFirst ? user.substring(1) : user);
            }
        }

        return null;
    }
}
