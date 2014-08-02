package tk.jomp16.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import tk.jomp16.plugin.event.Event;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class PluginLoader {
    public PluginRegister loadPlugin(File pluginFile) throws Exception {
        if (pluginFile.getName().endsWith(".jar")) {
            List<Event> events = new ArrayList<>();

            URL[] urls = new URL[]{pluginFile.toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());

            Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(new URL("file:" + pluginFile.getPath())).addClassLoader(urlClassLoader));
            Set<Class<? extends Event>> eventsClasses = reflections.getSubTypesOf(Event.class);

            for (Class<? extends Event> eventClass : eventsClasses) {
                Constructor<? extends Event> eventConstructor = eventClass.getConstructor();

                Event event = eventConstructor.newInstance();

                events.add(event);
            }

            return new PluginRegister(urlClassLoader, events);
        } else {
            throw new UnsupportedOperationException("File isn't .jar!");
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class PluginRegister {
        private final URLClassLoader urlClassLoader;
        private final List<Event> events;
    }
}
