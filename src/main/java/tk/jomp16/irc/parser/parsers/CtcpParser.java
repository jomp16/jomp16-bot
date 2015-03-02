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
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.CtcpHandler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;

@Log4j2
public class CtcpParser implements Parser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        log.debug(parserToken);

        CtcpCommand ctcpCommand = CtcpCommand.getCtcpCommandFromString(parserToken.getParams().get(0).split(" ")[0]);

        User user = ircManager.getUserList().getUserFromHost(parserToken.getSource().getRawSource());
        Channel channel;

        if (parserToken.getParams().get(0).startsWith("#")) {
            channel = new Channel(ircManager, parserToken.getParams().remove(0));
        } else {
            channel = new Channel(ircManager, user.getNickName());
        }

        return new CtcpHandler(ircManager, user, channel, ctcpCommand, parserToken.getParams().get(0));
    }

    public enum CtcpCommand {
        TIME,
        DCC,
        PING,
        VERSION;

        public static CtcpCommand getCtcpCommandFromString(String command) {
            if (command.startsWith("\001")) {
                command = command.replace("\001", "");

                return CtcpCommand.valueOf(command);
            } else {
                return null;
            }
        }
    }
}
