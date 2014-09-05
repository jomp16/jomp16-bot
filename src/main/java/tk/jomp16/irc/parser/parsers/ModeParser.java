/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser.parsers;

import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.ModeHandler;
import tk.jomp16.irc.modes.Mode;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;

@Log4j2
public class ModeParser implements Parser {
    private boolean firstMode = true;

    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (firstMode) {
            // join after first IRC mode, which is always given by IRC server (probably?)
            firstMode = false;
            ircManager.joinAllChannels();

            return null;
        }

        //if (parserToken.getRawLine().contains(ircManager.getConfiguration().getNick() + " :+i")) {
        //    ircManager.joinAllChannels();
        //    return null;
        //}

        /*if (parserToken.getSource().getNick().equals(ircManager.getConfiguration().getNick())) {
            return null;
        }*/

        if (parserToken.getParams().size() > 2) {
            User user = ircManager.getUserList().getUserFromHost(parserToken.getSource().getRawSource());

            Channel channel = new Channel(ircManager, parserToken.getParams().get(0));
            Mode mode = Mode.getMode(parserToken.getParams().get(1));
            String userModded = parserToken.getParams().get(2);

            ircManager.getChannelList().changeUserLevel(channel.getTargetName(), userModded, mode);

            return new ModeHandler(ircManager, user, channel, mode, userModded);
        }

        return null;
    }
}
