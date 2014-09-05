/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.parser.parsers.*;
import tk.jomp16.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class IrcParser {
    private final Map<IrcCommand, Parser> commandParsers = new HashMap<IrcCommand, Parser>() {{
        MotdParser motdParser = new MotdParser();
        ChannelInfoParser channelInfoParser = new ChannelInfoParser();
        WhoisInfoParser whoisInfoParser = new WhoisInfoParser();
        CapParser capParser = new CapParser();
        NickNameInUseParser nickNameInUseParser = new NickNameInUseParser();

        put(IrcCommand.RESPONSE_MOTD_START, motdParser);
        put(IrcCommand.RESPONSE_MOTD_CONTENT, motdParser);
        put(IrcCommand.RESPONSE_MOTD_END, motdParser);
        put(IrcCommand.COMMAND_NOTICE, new NoticeParser());
        put(IrcCommand.COMMAND_MODE, new ModeParser());
        put(IrcCommand.COMMAND_PRIVMSG, new PrivMsgParser());
        put(IrcCommand.COMMAND_PING, new PingParser());
        put(IrcCommand.COMMAND_TOPIC, new ChannelTopicParser());
        put(IrcCommand.RESPONSE_TOPIC_MESSAGE, channelInfoParser);
        put(IrcCommand.RESPONSE_TOPIC_SETBY, channelInfoParser);
        put(IrcCommand.RESPONSE_NAMES_LIST, new ChannelNamesParser());
        put(IrcCommand.COMMAND_JOIN, new JoinParser());
        put(IrcCommand.COMMAND_PART, new PartParser());
        put(IrcCommand.COMMAND_QUIT, new QuitParser());
        put(IrcCommand.COMMAND_KICK, new KickParser());
        put(IrcCommand.RESPONSE_WHOIS_HOSTMASK, whoisInfoParser);
        put(IrcCommand.RESPONSE_CAP, capParser);
        put(IrcCommand.RESPONSE_AUTHENTICATE, capParser);
        put(IrcCommand.RESPONSE_AUTHENTICATE_SUCCESS, capParser);
        put(IrcCommand.RESPONSE_AUTHENTICATE_ERROR1, capParser);
        put(IrcCommand.RESPONSE_AUTHENTICATE_ERROR2, capParser);
        put(IrcCommand.COMMAND_NICK, new NickParser());
        put(IrcCommand.ERROR_NICK_IN_USE, nickNameInUseParser);
        put(IrcCommand.ERROR_NICK_UNAVAILABLE, nickNameInUseParser);
    }};
    private String host;

    public void parse(@NonNull IrcManager ircManager, @NonNull String rawIrcLine) {
        List<String> parsedLine = Utils.tokenizeLine(rawIrcLine);

        if (host == null) {
            host = parsedLine.get(0);
        }

        if (!parsedLine.get(0).startsWith(":")) {
            parsedLine.add(0, host);
        }

        Source source = new Source(parsedLine.remove(0).replace(":", ""));
        String command = parsedLine.remove(0).toUpperCase();
        ParserToken parserToken = new ParserToken(rawIrcLine, source, command, parsedLine);
        IrcCommand ircCommand = IrcCommand.getIrcCommandFromCommandString(command);

        if (commandParsers.containsKey(ircCommand)) {
            Parser ircParser = commandParsers.get(ircCommand);
            Handler handler = ircParser.parse(ircManager, parserToken);

            if (handler != null) {
                try {
                    handler.respond();
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }
    }
}
