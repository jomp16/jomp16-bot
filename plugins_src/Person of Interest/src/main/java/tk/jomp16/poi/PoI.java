/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.poi;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.magicwerk.brownies.collections.GapList;
import tk.jomp16.irc.event.events.CommandEvent;
import tk.jomp16.irc.event.events.DisableEvent;
import tk.jomp16.irc.event.events.InitEvent;
import tk.jomp16.language.LanguageManager;
import tk.jomp16.plugin.command.Command;
import tk.jomp16.plugin.event.PluginEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
public class PoI extends PluginEvent {
    private LanguageManager languageManager;
    private List<PoIEpisode> poIEpisodes;
    private List<String> seasons;
    private DateTimeFormatter formatter;

    @Command(value = "poi", args = "print_size_list")
    public void poi(CommandEvent commandEvent) throws Exception {
        if (commandEvent.getOptionSet().has("print_size_list")) {
            commandEvent.respond("Currently PoI has " + String.valueOf(poIEpisodes.size() + " episodes."));
        } else {
            if (commandEvent.getArgs().size() > 0) {
                String fullName = commandEvent.getArgs().get(0).toUpperCase();

                poIEpisodes.stream().filter(poIEpisode -> poIEpisode.getFullName().equals(fullName)).limit(1).forEach(poIEpisode -> commandEvent.respond(languageManager.getAsString("episode.info", poIEpisode.getFullName(), poIEpisode.getName(), formatter.format(poIEpisode.getAirDate()), poIEpisode.getSynopsis())));
            } else {
                PoIEpisode poIEpisode = poIEpisodes.get(0);

                commandEvent.respond(languageManager.getAsString("episode.info", poIEpisode.getFullName(), poIEpisode.getName(), formatter.format(poIEpisode.getAirDate()), poIEpisode.getSynopsis()));
            }
        }
    }

    @Override
    public void onInit(InitEvent initEvent) throws Exception {
        this.languageManager = initEvent.getLanguageManager(this, "lang.Strings");
        this.poIEpisodes = new GapList<>();
        this.seasons = new GapList<>();
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Document document = Jsoup.connect(languageManager.getAsString("episodes.url.path")).userAgent(languageManager.getAsString("user.agent")).get();

        Elements elements = document.getElementsByClass("filters");

        if (elements.size() > 0) {
            Element element = elements.get(0);
            Elements elements1 = element.getElementsByTag("strong");

            if (elements1.size() > 0) {
                elements1.stream().filter(element2 -> !element2.text().toLowerCase().equals("all") && !element2.text().isEmpty()).forEach(element1 -> seasons.add(element1.text().toLowerCase().replace(" ", "-")));
            }
        }

        this.seasons.stream().forEach(season -> {
            try {
                int seasonNumber = Integer.parseInt(season.replaceAll("[\\D]", ""));

                Document document1 = Jsoup.connect(languageManager.getAsString("episodes.season.url.path", season)).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36").get();

                Elements elements1 = document1.getElementsByClass("episode");

                if (elements1.size() > 0) {
                    elements1.stream().forEach(element -> {
                        Elements elements2 = element.getElementsByClass("description");

                        if (elements2.size() > 0) {
                            String title = element.getElementsByClass("title").get(0).text();
                            int episodeNumber = Integer.parseInt(element.getElementsByClass("ep_info").get(0).text().replaceAll("[\\D]", ""));
                            String synopsis = elements2.get(0).text().replace(".moreless", ".");

                            LocalDate airDate = LocalDate.parse(element.getElementsByClass("date").get(0).text(), DateTimeFormatter.ofPattern("M/d/yy"));

                            this.poIEpisodes.add(new PoIEpisode(seasonNumber, episodeNumber, title, synopsis, airDate));
                        }
                    });
                }
            } catch (IOException e) {
                log.error(e, e);
            }
        });

        this.seasons.clear();
    }

    @Override
    public void onDisable(DisableEvent disableEvent) throws Exception {
        poIEpisodes.clear();
    }
}
