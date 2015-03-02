/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.currency;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.irc.event.events.InitEvent;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Currency extends PluginEvent {
    private LanguageManager languageManager;
    private String URL_CURRENCY = "http://rate-exchange.appspot.com/currency?from=%s&to=%s&q=%s";
    private Gson gson = new Gson();

    @Command(value = "currency", optCommands = "money")
    public void currency(CommandEvent commandEvent) throws Exception {
        if (commandEvent.getArgs().size() >= 3) {
            String quantity = commandEvent.getArgs().get(0).toUpperCase();
            String from = commandEvent.getArgs().get(1).toUpperCase();
            String to = commandEvent.getArgs().get(2).toUpperCase();

            URL url = new URL(String.format(URL_CURRENCY, from, to, quantity));

            CurrencyJSON currencyJSON;

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                 JsonReader reader = new JsonReader(bufferedReader)) {

                currencyJSON = gson.fromJson(reader, CurrencyJSON.class);
            }

            commandEvent.respond(languageManager.getAsString("currency.handle", quantity, from, to, currencyJSON.getV(), currencyJSON.getRate()));
        } else {
//            commandEvent.showUsage(this, commandEvent.getCommand());
        }
    }

    @Override
    public void onInit(InitEvent initEvent) {
        this.languageManager = initEvent.getLanguageManager(this, "lang.Strings");

        // todo: help
    }
}
