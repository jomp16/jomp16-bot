/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin.utils.currency;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import tk.jomp16.irc.listener.listeners.CommandListener;
import tk.jomp16.irc.listener.listeners.InitListener;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.Event;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Currency extends Event {
    private LanguageManager languageManager;
    private String URL_CURRENCY = "http://rate-exchange.appspot.com/currency?from=%s&to=%s&q=%s";
    private Gson gson = new Gson();

    @Command(value = "currency", optCommands = "money")
    public void currency(CommandListener commandListener) throws Exception {
        if (commandListener.getArgs().size() >= 3) {
            String quantity = commandListener.getArgs().get(0).toUpperCase();
            String from = commandListener.getArgs().get(1).toUpperCase();
            String to = commandListener.getArgs().get(2).toUpperCase();

            URL url = new URL(String.format(URL_CURRENCY, from, to, quantity));

            CurrencyJSON currencyJSON;

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                 JsonReader reader = new JsonReader(bufferedReader)) {

                currencyJSON = gson.fromJson(reader, CurrencyJSON.class);
            }

            commandListener.respond(languageManager.getAsString("currency.respond", quantity, from, to, currencyJSON.getV(), currencyJSON.getRate()));
        } else {
//            commandListener.showUsage(this, commandListener.getCommand());
        }
    }

    @Override
    public void onInit(InitListener initListener) {
        this.languageManager = initListener.getLanguageManager(this, "lang.Strings");

        // todo: help
    }
}
