package tk.jomp16.plugin;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.level.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Log4j2
@Getter
public class PluginManager extends Event {
    private IrcManager ircManager;
    private List<Plugin> plugins = new ArrayList<>();
    private Gson gson;
    private PluginLoader pluginLoader;

    public PluginManager(IrcManager ircManager) {
        this.ircManager = ircManager;
        this.gson = new Gson();
        this.pluginLoader = new PluginLoader();

        File f = new File("plugins/data");

        if (!f.exists()) {
            if (!f.mkdirs()) {
                log.error("Couldn't possible to create plugin directory");
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void loadAll() throws Exception {
        File pluginDirectory = new File("plugins");

        for (File pluginFile : pluginDirectory.listFiles()) {
            if (pluginFile.getName().endsWith(".jar")) {
                JarFile jarFile = new JarFile(pluginFile);
                JarEntry entry = jarFile.getJarEntry("plugin.json");

                if (entry != null) {
                    PluginLoader.PluginRegister pluginRegister = pluginLoader.loadPlugin(pluginFile);
                    PluginInfo pluginInfo = gson.fromJson(new InputStreamReader(pluginRegister.getUrlClassLoader().getResourceAsStream("plugin.json")), PluginInfo.class);

                    Plugin plugin = new Plugin(pluginInfo, DigestUtils.md5Hex(new FileInputStream(pluginFile)), pluginRegister.getEvents(), pluginRegister.getUrlClassLoader());

                    plugins.add(plugin);

                    ircManager.registerPlugin(plugin);
                }
            }
        }
    }

    /**
     * @param pluginName
     * @return 0 if success, 1 if plugin doesn't exists, 2 if plugin with same name and MD5 is already loaded
     * @throws Exception
     */
    public int load(String pluginName) throws Exception {
        File pluginFile = new File("plugins/" + pluginName + ".jar");

        if (pluginFile.exists()) {
            JarFile jarFile = new JarFile(pluginFile);
            JarEntry entry = jarFile.getJarEntry("plugin.json");

            if (entry != null) {
                PluginLoader.PluginRegister pluginRegister = pluginLoader.loadPlugin(pluginFile);
                PluginInfo pluginInfo = gson.fromJson(new InputStreamReader(pluginRegister.getUrlClassLoader().getResourceAsStream("plugin.json")), PluginInfo.class);
                Plugin plugin = new Plugin(pluginInfo, DigestUtils.md5Hex(new FileInputStream(pluginFile)), pluginRegister.getEvents(), pluginRegister.getUrlClassLoader());

                if (plugins.parallelStream().filter(plugin1 -> plugin1.getPluginInfo().getName().equals(plugin.getPluginInfo().getName())).count() == 0 ||
                        plugins.parallelStream().filter(plugin1 -> plugin1.getMd5sums().equals(plugin.getMd5sums())).count() == 0) {

                    plugins.add(plugin);

                    ircManager.registerPlugin(plugin);

                    return 0;
                } else {
                    plugin.getUrlClassLoader().close();

                    return 2;
                }
            }
        }

        return 1;
    }

    /**
     * @param pluginName
     * @return 0 if success, 1 if no plugin found
     * @throws Exception
     */
    public int unload(String pluginName) throws Exception {
        if (plugins.parallelStream().filter(plugin -> plugin.getPluginInfo().getName().equals(pluginName)).count() != 0) {
            for (Plugin plugin1 : plugins.parallelStream().filter(plugin -> plugin.getPluginInfo().getName().equals(pluginName)).collect(Collectors.toList())) {
                ircManager.unregisterPlugin(plugin1);
                plugins.remove(plugin1);
                plugin1.getUrlClassLoader().close();
            }

            return 0;
        }

        return 1;
    }

    /**
     * @param pluginName
     * @return 0 if success, 1 if no plugin, 2 if plugin won't need reload
     * @throws Exception
     */
    public int reload(String pluginName) throws Exception {
        File pluginFile = new File("plugins/" + pluginName + ".jar");

        if (pluginFile.exists()) {
            JarFile jarFile = new JarFile(pluginFile);
            JarEntry entry = jarFile.getJarEntry("plugin.json");

            if (entry != null) {
                PluginLoader.PluginRegister pluginRegister = pluginLoader.loadPlugin(pluginFile);
                PluginInfo pluginInfo = gson.fromJson(new InputStreamReader(pluginRegister.getUrlClassLoader().getResourceAsStream("plugin.json")), PluginInfo.class);
                Plugin plugin = new Plugin(pluginInfo, DigestUtils.md5Hex(new FileInputStream(pluginFile)), pluginRegister.getEvents(), pluginRegister.getUrlClassLoader());

                if (plugins.parallelStream().filter(plugin1 -> plugin1.getPluginInfo().getName().equals(plugin.getPluginInfo().getName())).count() != 0 &&
                        plugins.parallelStream().filter(plugin1 -> plugin1.getMd5sums().equals(plugin.getMd5sums())).count() == 0) {
                    for (Plugin plugin1 : plugins.parallelStream().filter(plugin2 -> plugin2.getPluginInfo().getName().equals(plugin.getPluginInfo().getName())).collect(Collectors.toList())) {
                        ircManager.unregisterPlugin(plugin1);
                        plugins.remove(plugin1);
                        plugin1.getUrlClassLoader().close();

                        plugins.add(plugin);
                        ircManager.registerPlugin(plugin);
                    }

                    return 0;
                } else {
                    plugin.getUrlClassLoader().close();

                    return 2;
                }
            }
        }

        return 1;
    }

    /*private InputStreamReader getPluginInfoInputStream(File file) throws Exception {
        log.error(file);

        URL url = new URL("jar:file:" + file.getPath() + "!/plugin.json");

        return new InputStreamReader(url.openStream());
    }*/

    @Command(value = "plugin", level = Level.OWNER, args = {"load:", "reload:", "unload:"})
    public void plugin(CommandListener commandListener) throws Exception {
        if (commandListener.getOptionSet().has("load")) {
            int result = commandListener.getIrcManager().getPluginManager().load((String) commandListener.getOptionSet().valueOf("load"));

            switch (result) {
                case 0:
                    commandListener.respond("Success!");
                    break;
                case 1:
                    commandListener.respond("Plugin doesn't exists!");
                    break;
                case 2:
                    commandListener.respond("Plugin already loaded!");
                    break;
            }
        } else if (commandListener.getOptionSet().has("reload")) {
            int result = commandListener.getIrcManager().getPluginManager().reload((String) commandListener.getOptionSet().valueOf("reload"));

            switch (result) {
                case 0:
                    commandListener.respond("Success!");
                    break;
                case 1:
                    commandListener.respond("Plugin doesn't exists!");
                    break;
                case 2:
                    commandListener.respond("Plugin won't need reload!");
                    break;
            }
        } else if (commandListener.getOptionSet().has("unload")) {
            int result = commandListener.getIrcManager().getPluginManager().unload((String) commandListener.getOptionSet().valueOf("unload"));

            switch (result) {
                case 0:
                    commandListener.respond("Success!");
                    break;
                case 1:
                    commandListener.respond("Plugin not found!");
                    break;
            }
        }
    }
}
