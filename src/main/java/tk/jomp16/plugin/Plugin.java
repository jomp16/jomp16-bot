package tk.jomp16.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tk.jomp16.plugin.event.Event;

import java.net.URLClassLoader;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Plugin {
    private final PluginInfo pluginInfo;
    private final String md5sums;
    private final List<Event> events;
    private final URLClassLoader urlClassLoader;
}
