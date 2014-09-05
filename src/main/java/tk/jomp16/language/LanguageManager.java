/*
 * Copyright © 2014 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.language;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.text.MessageFormat;
import java.util.*;

public class LanguageManager {
    private ResourceBundle resourceBundle;
    private ResourceBundleControl resourceBundleControl;

    public LanguageManager(String path) {
        resourceBundleControl = new ResourceBundleControl();

        resourceBundle = ResourceBundle.getBundle(path, resourceBundleControl);
    }

    public LanguageManager(String path, ClassLoader classLoader) {
        resourceBundleControl = new ResourceBundleControl();

        resourceBundle = ResourceBundle.getBundle(path, Locale.getDefault(), classLoader, resourceBundleControl);
    }

    public String getAsString(String key) {
        if (resourceBundleControl.isJSON()) {
            return this.getAsJsonElement(key).getAsString();
        } else {
            return resourceBundle.getString(key);
        }
    }

    public String getAsString(String key, Object... params) {
        if (resourceBundleControl.isJSON()) {
            return MessageFormat.format(this.getAsJsonElement(key).getAsString(), params);
        } else {
            return MessageFormat.format(resourceBundle.getString(key), params);
        }
    }

    public Object getAsObject(String key) {
        return resourceBundle.getObject(key);
    }

    public JsonElement getAsJsonElement(String key) {
        if (resourceBundleControl.isJSON()) {
            return (JsonElement) this.getAsObject(key);
        } else {
            throw new UnsupportedOperationException("LanguageManager isn't JSON!");
        }
    }

    public List<JsonElement> getArrayAsJsonElement(String key) {
        if (resourceBundleControl.isJSON()) {
            List<JsonElement> elements = new ArrayList<>();

            JsonArray jsonArray = (JsonArray) this.getAsObject(key);

            jsonArray.forEach(elements::add);

            return elements;
        } else {
            throw new UnsupportedOperationException("LanguageManager isn't JSON!");
        }
    }

    public List<String> getArrayAsString(String key) {
        if (resourceBundleControl.isJSON()) {
            List<String> elements = new ArrayList<>();

            JsonArray jsonArray = (JsonArray) this.getAsObject(key);

            jsonArray.forEach(element -> elements.add(element.getAsString()));

            return elements;
        } else {
            return Arrays.asList(resourceBundle.getStringArray(key));
        }
    }

    public List<String> getArrayAsString(String key, Object... params) {
        if (resourceBundleControl.isJSON()) {
            List<String> elements = new ArrayList<>();

            JsonArray jsonArray = (JsonArray) this.getAsObject(key);

            jsonArray.forEach(element -> {
                elements.add(MessageFormat.format(element.getAsString(), params));
            });

            return elements;
        } else {
            List<String> elements = new ArrayList<>();

            for (String tmp : resourceBundle.getStringArray(key)) {
                elements.add(MessageFormat.format(tmp, params));
            }

            return elements;
        }
    }

    public <T> List<T> getJsonArrayAsClass(String key, Class<T> tClass) {
        if (resourceBundleControl.isJSON()) {
            List<T> arrayList = new ArrayList<>();

            this.getArrayAsJsonElement(key).forEach(element -> {
                String tmp = resourceBundleControl.getGson().toJson(element);
                arrayList.add(resourceBundleControl.getGson().fromJson(tmp, tClass));
            });

            return arrayList;
        } else {
            throw new UnsupportedOperationException("LanguageManager isn't JSON!");
        }
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public ResourceBundleControl getResourceBundleControl() {
        return resourceBundleControl;
    }
}
