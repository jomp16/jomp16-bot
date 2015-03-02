/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.magicwerk.brownies.collections.GapList;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.level.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Log4j2
@Getter
public class PluginManager extends PluginEvent {
    private static boolean alreadyLoaded = false;
    private IrcManager ircManager;
    private List<Plugin> plugins = new GapList<>();
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
        if (!alreadyLoaded) {
            alreadyLoaded = true;

            File pluginDirectory = new File("plugins");

            for (File pluginFile : pluginDirectory.listFiles()) {
                if (pluginFile.getName().endsWith(".jar")) {
                    JarFile jarFile = new JarFile(pluginFile);
                    JarEntry entry = jarFile.getJarEntry("plugin.json");

                    if (entry != null) {
                        PluginLoader.PluginRegister pluginRegister = pluginLoader.loadPlugin(pluginFile);
                        PluginInfo pluginInfo = gson.fromJson(new InputStreamReader(pluginRegister.getUrlClassLoader().getResourceAsStream("plugin.json")), PluginInfo.class);

                        Plugin plugin = new Plugin(pluginInfo, DigestUtils.md5Hex(new FileInputStream(pluginFile)), pluginRegister.getPluginEvents(), pluginRegister.getUrlClassLoader());

                        plugins.add(plugin);

                        ircManager.registerPlugin(plugin);
                    }
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
                Plugin plugin = new Plugin(pluginInfo, DigestUtils.md5Hex(new FileInputStream(pluginFile)), pluginRegister.getPluginEvents(), pluginRegister.getUrlClassLoader());

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
            String md5 = DigestUtils.md5Hex(new FileInputStream(pluginFile));
            String tmp = pluginName.substring(0, pluginName.indexOf('-'));

            if (plugins.parallelStream().filter(plugin -> plugin.getPluginInfo().getName().equals(tmp)).count() != 0 &&
                    plugins.parallelStream().filter(plugin1 -> plugin1.getMd5sums().equals(md5)).count() == 0) {
                int tmp1 = unload(pluginName.substring(0, pluginName.indexOf('-')));

                if (tmp1 == 0) {
                    return load(pluginName);
                } else {
                    return tmp1;
                }
            }
        }

        return 1;
    }

    @Command(value = "plugin", level = Level.OWNER, args = {"load:", "reload:", "unload:"})
    public void plugin(CommandEvent commandEvent) throws Exception {
        if (commandEvent.getOptionSet().has("load")) {
            int result = commandEvent.getIrcManager().getPluginManager().load((String) commandEvent.getOptionSet().valueOf("load"));

            switch (result) {
                case 0:
                    commandEvent.respond("Success!");
                    break;
                case 1:
                    commandEvent.respond("Plugin doesn't exists!");
                    break;
                case 2:
                    commandEvent.respond("Plugin already loaded!");
                    break;
            }
        } else if (commandEvent.getOptionSet().has("reload")) {
            int result = commandEvent.getIrcManager().getPluginManager().reload((String) commandEvent.getOptionSet().valueOf("reload"));

            switch (result) {
                case 0:
                    commandEvent.respond("Success!");
                    break;
                case 1:
                    commandEvent.respond("Plugin doesn't exists!");
                    break;
                case 2:
                    commandEvent.respond("Plugin won't need reload!");
                    break;
            }
        } else if (commandEvent.getOptionSet().has("unload")) {
            int result = commandEvent.getIrcManager().getPluginManager().unload((String) commandEvent.getOptionSet().valueOf("unload"));

            switch (result) {
                case 0:
                    commandEvent.respond("Success!");
                    break;
                case 1:
                    commandEvent.respond("Plugin not found!");
                    break;
            }
        }
    }
}
