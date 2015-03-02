/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.language;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.NonNull;
import org.magicwerk.brownies.collections.GapList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ResourceBundleControl extends ResourceBundle.Control {
    private final String FORMAT_JSON = "json";
    private final List<String> FORMATS = new GapList<>();
    private Gson gson;

    public ResourceBundleControl() {
        FORMATS.add(FORMAT_JSON);
        gson = new Gson();
    }

    @Override
    public List<String> getFormats(String baseName) {
        return FORMATS;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        if (!FORMAT_JSON.equals(format)) {
            return null;
        }

        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, format);

        InputStream is = loader.getResourceAsStream(resourceName);

        if (is == null) {
            return null;
        }

        JsonObject jsonObject;

        String CHARSET = "UTF-8";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET))) {
            jsonObject = gson.fromJson(br, JsonObject.class);
        }

        JSONResourceBundle rb = new JSONResourceBundle();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            rb.put(entry.getKey(), entry.getValue());
        }

        return rb;
    }

    public Gson getGson() {
        return gson;
    }

    private static class JSONResourceBundle extends ResourceBundle {
        private Map<String, JsonElement> data = new HashMap<>();

        @Override
        @NonNull
        public Enumeration<String> getKeys() {
            return Collections.enumeration(data.keySet());
        }

        @Override
        protected Object handleGetObject(@NonNull String key) {
            return data.get(key);
        }

        public void put(String key, JsonElement value) {
            data.put(key, value);
        }
    }
}
