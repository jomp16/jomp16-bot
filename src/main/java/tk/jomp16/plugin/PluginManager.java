/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
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
    private static boolean alreadyLoaded = false;
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

                        Plugin plugin = new Plugin(pluginInfo, DigestUtils.md5Hex(new FileInputStream(pluginFile)), pluginRegister.getEvents(), pluginRegister.getUrlClassLoader());

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
        // todo: 
        /* java.lang.reflect.InvocationTargetException
                at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:1.8.0_11]
                at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:1.8.0_11]
                at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:1.8.0_11]
                at java.lang.reflect.Method.invoke(Method.java:483) ~[?:1.8.0_11]
                at tk.jomp16.irc.handler.handlers.PrivMsgHandler.invoke(PrivMsgHandler.java:171) [jomp16-bot-0.1.jar:0.1]
                at tk.jomp16.irc.handler.handlers.PrivMsgHandler.invoke(PrivMsgHandler.java:160) [jomp16-bot-0.1.jar:0.1]
                at tk.jomp16.irc.handler.handlers.PrivMsgHandler.lambda$respond$39(PrivMsgHandler.java:82) [jomp16-bot-0.1.jar:0.1]
                at tk.jomp16.irc.handler.handlers.PrivMsgHandler$$Lambda$15/2008916272.run(Unknown Source) [jomp16-bot-0.1.jar:0.1]
                at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) [?:1.8.0_11]
                at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) [?:1.8.0_11]
                at java.lang.Thread.run(Thread.java:745) [?:1.8.0_11]
            Caused by: java.lang.NullPointerException
                at java.io.Reader.<init>(Reader.java:78) ~[?:1.8.0_11]
                at java.io.InputStreamReader.<init>(InputStreamReader.java:72) ~[?:1.8.0_11]
                at tk.jomp16.plugin.PluginManager.reload(PluginManager.java:136) ~[jomp16-bot-0.1.jar:0.1]
                at tk.jomp16.plugin.PluginManager.plugin(PluginManager.java:187) ~[jomp16-bot-0.1.jar:0.1]
                ... 11 more */

        /* File pluginFile = new File("plugins/" + pluginName + ".jar");

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

        return 1; */

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
