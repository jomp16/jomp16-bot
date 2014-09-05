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
import tk.jomp16.irc.parser.Parser;
import tk.jomp16.irc.parser.ParserToken;

public class CapParser implements Parser {
    @Override
    public Handler parse(IrcManager ircManager, ParserToken parserToken) {
        if (parserToken.getCommand().equals("CAP")) {
            String command = parserToken.getParams().get(1);

            switch (command) {
                case "LS":
                    String[] capSplitted = parserToken.getParams().get(2).split(" ");

                    for (String cap : capSplitted) {
                        if (cap.equals("sasl")) {
                            if (ircManager.getConfiguration().isSasl()) {
                                ircManager.proceedSaslAuth(0);
                            }
                        }
                    }
                    break;
                case "ACK":
                    if (ircManager.getConfiguration().isSasl() && parserToken.getParams().get(2).contains("sasl")) {
                        ircManager.proceedSaslAuth(1);
                    }

                    break;
            }
        } else if (parserToken.getCommand().equals("AUTHENTICATE")) {
            if (ircManager.getConfiguration().isSasl()) {
                ircManager.proceedSaslAuth(2);
            }
            //} else if (parserToken.getCommand().equals("904") || parserToken.getCommand().equals("905")) {
            // todo ?
            //} else if (parserToken.getCommand().equals("903")) {
            // ircManager.getOutputCap().sendEnd();
        } else {
            ircManager.getOutputCap().sendEnd();
        }

        return null;
    }
}
