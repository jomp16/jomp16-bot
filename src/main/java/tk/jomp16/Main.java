/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import tk.jomp16.configuration.Configuration;
import tk.jomp16.irc.IrcManager;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Log4j2
public class Main {
    private static Map<String, IrcManager> ircManagerMap = new HashMap<>();
    private static Executor executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        log.info("Welcome to jomp16-bot!");

        try (FileReader fileReader = new FileReader("config.json")) {
            List<Configuration.Builder> configurations = new Gson().fromJson(fileReader, new TypeToken<List<Configuration.Builder>>() {
            }.getType());

            configurations.parallelStream().forEach(configBuilder -> {
                Runnable runnable = () -> {
                    log.debug("Connecting into " + configBuilder.server());

                    try {
                        IrcManager ircManager = new IrcManager(configBuilder.build());
                        ircManager.startIrc();
                        ircManagerMap.put(UUID.randomUUID().toString(), ircManager);
                    } catch (Exception e) {
                        log.error("Error when trying to start IrcManager on Main!", e);
                    }
                };

                executor.execute(runnable);
            });
        }
    }
}