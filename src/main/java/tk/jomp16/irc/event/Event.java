/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.irc.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.irc.IrcManager;
import tk.jomp16.irc.channel.Channel;
import tk.jomp16.irc.user.User;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.PluginInfo;
import tk.jomp16.plugin.event.PluginEvent;
import tk.jomp16.plugin.help.HelpRegister;

import java.io.File;
import java.net.URLDecoder;

@Getter
@RequiredArgsConstructor
@Log4j2
public class Event {
    protected final IrcManager ircManager;
    protected final User user;
    protected final Channel channel;
    protected final PluginEvent pluginEvent;

    @Synchronized
    public void respond(String message) {
        ircManager.getOutputIrc().sendPrivMsg(channel.getTargetName(), user.getNickName(), message);
    }

    @Synchronized
    public void respond(String message, boolean showName) {
        if (showName) {
            respond(message);
        } else {
            ircManager.getOutputIrc().sendPrivMsg(channel.getTargetName(), message);
        }
    }

    @Synchronized
    public void respond(String target, String message) {
        respond(target, message, false);
    }

    @Synchronized
    public void respond(String target, String user, String message) {
        ircManager.getOutputIrc().sendPrivMsg(target, user, message);
    }

    @Synchronized
    public void respond(String target, String message, boolean pingUser) {
        if (pingUser) {
            ircManager.getOutputIrc().sendPrivMsg(channel.getTargetName(), target, message);
        } else {
            ircManager.getOutputIrc().sendPrivMsg(target, message);
        }
    }

    @Synchronized
    public void notice(String message) {
        ircManager.getOutputIrc().sendNotice(user.getNickName(), message);
    }

    @Synchronized
    public void notice(String target, String message) {
        ircManager.getOutputIrc().sendNotice(target, message);
    }

    public LanguageManager getLanguageManager(PluginEvent pluginEvent, String resourcePath) {
        return new LanguageManager(resourcePath, pluginEvent.getClass().getClassLoader());
    }

    public String getStringPluginPath(PluginEvent pluginEvent) {
        return getFilePluginPath(pluginEvent).getPath();
    }

    public String getStringPluginPath(PluginEvent pluginEvent, String addPath) {
        return getFilePluginPath(pluginEvent, addPath).getPath();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getFilePluginPath(PluginEvent pluginEvent) {
        File file = new File("plugins/data/" + pluginEvent.getClass().getSimpleName());

        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getFilePluginPath(PluginEvent pluginEvent, String addPath) {
        File f = new File(getFilePluginPath(pluginEvent), addPath);

        if (!f.exists()) {
            f.mkdirs();
        }

        return f;
    }

    public PluginInfo getPluginInfo() {
        return ircManager.getPluginInfoFromEvent(pluginEvent);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
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

    public String getPluginJarPath(PluginEvent pluginEvent) {
        try {
            return URLDecoder.decode(pluginEvent.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    public void addHelp(HelpRegister helpRegister) {
        pluginEvent.registerHelp(helpRegister);
    }
}
