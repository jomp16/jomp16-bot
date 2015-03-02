/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser.parsers;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.channel.ChannelLevel;
import tk.jomp16.irc.handler.Handler;
import tk.jomp16.irc.handler.handlers.JoinHandler;
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;
import tk.jomp16.irc.user.User;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class JoinParser implements Parser {
    private Map<String, LocalTime> holder = new HashMap<>();

    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getSource().getNick().equals(ircManager.getConfiguration().getNick())) {
            return null;
        }

        User user = ircManager.getUserList().getUserFromHost(parserToken.getSource().getRawSource());

        if (user.getRealName() == null) {
            if (holder.containsKey(user.getNickName())) {
                if (LocalTime.now().compareTo(holder.get(user.getNickName())) > 60) {
                    holder.put(user.getNickName(), LocalTime.now());

                    ircManager.getOutputIrc().sendWhois(user.getNickName());
                }
            } else {
                holder.put(user.getNickName(), LocalTime.now());

                ircManager.getOutputIrc().sendWhois(user.getNickName());
            }
        }

        Channel channel;

        if (parserToken.getParams().get(0).startsWith("#")) {
            channel = new Channel(ircManager, parserToken.getParams().get(0));
        } else {
            return null;
        }

        ircManager.getChannelList().addUserToChannel(channel.getTargetName(), user, ChannelLevel.NORMAL);

        return new JoinHandler(ircManager, user, channel);
    }
}
