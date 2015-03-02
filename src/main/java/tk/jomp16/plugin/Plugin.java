/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tk.jomp16.plugin.event.PluginEvent;

import java.net.URLClassLoader;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Plugin {
    private final PluginInfo pluginInfo;
    private final String md5sums;
    private final List<PluginEvent> pluginEvents;
    private final URLClassLoader urlClassLoader;
}
