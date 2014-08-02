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
