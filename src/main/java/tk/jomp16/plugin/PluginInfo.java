/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.plugin;

import java.util.List;

public class PluginInfo {
    private String name;
    private String author;
    private List<String> authors;
    private String version;
    private String description;
    private String website;

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }
}
