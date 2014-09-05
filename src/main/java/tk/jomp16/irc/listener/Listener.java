/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.user.User;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.event.Event;
import tk.jomp16.plugin.help.HelpRegister;

import java.io.File;
import java.net.URLDecoder;

@Getter
@RequiredArgsConstructor
@Log4j2
public class Listener {
    protected final IrcManager ircManager;
    protected final User user;
    protected final Channel channel;
    protected final Event event;

    @Synchronized
    public synchronized void respond(Object message) {
        ircManager.getOutputIrc().sendPrivMsg(channel.getTargetName(), user.getNickName(), message);

        try {
            wait(700);
        } catch (Exception e) {
            log.error("An error happened in Listener!", e);
        }
    }

    @Synchronized
    public synchronized void respond(Object message, boolean showName) {
        if (showName) {
            respond(message);
        } else {
            ircManager.getOutputIrc().sendPrivMsg(channel.getTargetName(), message);
        }

        try {
            wait(700);
        } catch (Exception e) {
            log.error("An error happened in Listener!", e);
        }
    }

    @Synchronized
    public synchronized void respond(Object target, Object message) {
        ircManager.getOutputIrc().sendPrivMsg(target, message);

        try {
            wait(700);
        } catch (Exception e) {
            log.error("An error happened in Listener!", e);
        }
    }

    @Synchronized
    public synchronized void respond(Object target, Object user, Object message) {
        ircManager.getOutputIrc().sendPrivMsg(target, user, message);

        try {
            wait(700);
        } catch (Exception e) {
            log.error("An error happened in Listener!", e);
        }
    }

    @Synchronized
    public synchronized void respond(String user, Object message) {
        ircManager.getOutputIrc().sendPrivMsg(channel.getTargetName(), user, message);

        try {
            wait(700);
        } catch (Exception e) {
            log.error("An error happened in Listener!", e);
        }
    }

    public LanguageManager getLanguageManager(Event event, String resourcePath) {
        return new LanguageManager(resourcePath, event.getClass().getClassLoader());
    }

    public String getStringPluginPath(Event event) {
        return getFilePluginPath(event).getPath();
    }

    public String getStringPluginPath(Event event, String addPath) {
        return getFilePluginPath(event, addPath).getPath();
    }

    public File getFilePluginPath(Event event) {
        File file = new File("plugins/data/" + event.getClass().getSimpleName());

        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    public File getFilePluginPath(Event event, String addPath) {
        File f = new File(getFilePluginPath(event), addPath);

        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }

    public PluginInfo getPluginInfo() {
        return ircManager.getPluginInfoFromEvent(event);
    }

    public File getPluginPath(String addPath) {
        if (getPluginInfo() != null) {
            File f = new File(getPluginPath(), addPath);

            if (!f.exists()) {
                f.mkdirs();
            }

            return f;
        }

        throw new UnsupportedOperationException("PluginInfo is null!");
    }

    public File getPluginPath() {
        if (getPluginInfo() != null) {
            File f = new File("plugins/data/" + getPluginInfo().getName());

            if (!f.exists()) {
                f.mkdirs();
            }

            return f;
        }

        throw new UnsupportedOperationException("PluginInfo is null!");
    }

    public String getPluginJarPath(Event event) {
        try {
            return URLDecoder.decode(event.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    public void addHelp(HelpRegister helpRegister) {
        event.registerHelp(helpRegister);
    }
}
