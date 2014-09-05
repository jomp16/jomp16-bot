/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.configuration;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Configuration {
    @Setter
    private String nick;
    private String ident;
    private String realName;
    private String server;
    private int port = 6667;
    private boolean sasl;
    private String saslUser;
    private String password;
    private String prefix;
    private List<String> channels;
    private List<String> owners;
    private List<String> admins;
    private List<String> mods;

    public Configuration(Builder builder) {
        this.nick = builder.nick();
        this.ident = builder.ident();
        this.realName = builder.realName();
        this.server = builder.server();
        this.port = builder.port();
        this.sasl = builder.sasl();
        this.saslUser = builder.saslUser();
        this.password = builder.password();
        this.prefix = builder.prefix();
        this.channels = builder.channels();
        this.owners = builder.owners();
        this.admins = builder.admins();
        this.mods = builder.mods();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Accessors(chain = true, fluent = true)
    @Setter
    @Getter
    @ToString
    public static class Builder {
        private String nick = "jomp16-bot";
        private String ident = "jomp16-bot";
        @SerializedName("real_name")
        private String realName = "jomp16-bot";
        private String server = "irc.freenode.org";
        private boolean sasl = false;
        @SerializedName("sasl_user")
        private String saslUser = "placeholder";
        private String password = "placeholder";
        private String prefix = "*";
        private List<String> channels = new ArrayList<>();
        private List<String> owners = new ArrayList<>();
        private List<String> admins = new ArrayList<>();
        private List<String> mods = new ArrayList<>();
        private int port = 6667;

        public Builder addChannel(String channel) {
            channels.add(channel);

            return this;
        }

        public Builder addOwner(String mask) {
            owners.add(mask);

            return this;
        }

        public Builder addAdmin(String mask) {
            admins.add(mask);

            return this;
        }

        public Builder addMod(String mask) {
            mods.add(mask);

            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
