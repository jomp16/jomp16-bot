/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.modes;

public enum Mode {
    NORMAL(""),
    VOICE("+v"),
    OP("+o"),
    DEVOICE("-v"),
    DEOP("-o");
    private String mode;

    private Mode(String s) {
        this.mode = s;
    }

    public static Mode getMode(String mode) {
        for (Mode modes : values()) {
            if (modes.mode.equals(mode)) {
                return modes;
            }
        }

        return NORMAL;
    }
}