/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tk.jomp16.irc.user.User;

@AllArgsConstructor
@Getter
@Setter
public class ChannelUser {
    private User user;
    private ChannelLevel channelLevel;
}
