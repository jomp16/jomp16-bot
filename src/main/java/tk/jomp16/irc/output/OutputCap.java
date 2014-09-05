/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.output;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.jomp16.irc.IrcManager;

@RequiredArgsConstructor
public class OutputCap {
    private final IrcManager ircManager;

    public void sendReq(@NonNull String capRequested) {
        ircManager.getOutputRaw().writeRaw("CAP REQ :" + capRequested);
    }

    public void sendList() {
        ircManager.getOutputRaw().writeRaw("CAP LS");
    }

    public void sendEnd() {
        ircManager.getOutputRaw().writeRaw("CAP END");
    }
}
