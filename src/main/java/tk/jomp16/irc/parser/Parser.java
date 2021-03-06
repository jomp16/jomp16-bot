/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.parser;

import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.handler.Handler;

public interface Parser {
    public Handler parse(IrcManager ircManager, ParserToken parserToken);
}
