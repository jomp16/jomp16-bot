/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.jomp16.plugin.level.Level;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {
    private String nickName;
    private String hostName;
    private String hostMask;
    private String realName;
    private Level level;

    public String getCompleteHost() {
        return hostName + "@" + hostMask;
    }

    public String getCompleteRawLine() {
        return nickName + "!" + getCompleteHost();
    }
}
