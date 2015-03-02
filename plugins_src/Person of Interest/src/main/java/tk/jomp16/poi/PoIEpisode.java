/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.poi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
public class PoIEpisode {
    private final int season;
    private final int episode;
    private final String name;
    private final String synopsis;
    private final LocalDate airDate;

    public String getFullName() {
        return "S" + String.format("%02d", season) + "E" + String.format("%02d", episode);
    }
}
