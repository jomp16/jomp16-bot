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
import org.magicwerk.brownies.collections.GapList;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import tk.jomp16.plugin.event.PluginEvent;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

@Getter
public class PluginLoader {
    public PluginRegister loadPlugin(File pluginFile) throws Exception {
        if (pluginFile.getName().endsWith(".jar")) {
            List<PluginEvent> pluginEvents = new GapList<>();

            URL[] urls = new URL[]{pluginFile.toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

            Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(new URL("file:" + pluginFile.getPath())).addClassLoader(urlClassLoader));
            Set<Class<? extends PluginEvent>> eventsClasses = reflections.getSubTypesOf(PluginEvent.class);

            for (Class<? extends PluginEvent> eventClass : eventsClasses) {
                Constructor<? extends PluginEvent> eventConstructor = eventClass.getConstructor();

                PluginEvent pluginEvent = eventConstructor.newInstance();

                pluginEvents.add(pluginEvent);
            }

            return new PluginRegister(urlClassLoader, pluginEvents);
        } else {
            throw new UnsupportedOperationException("File isn't .jar!");
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class PluginRegister {
        private final URLClassLoader urlClassLoader;
        private final List<PluginEvent> pluginEvents;
    }
}
