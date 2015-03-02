/*
 * Copyright © 2015 jomp16 <joseoliviopedrosa@gmail.com>
 *
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package tk.jomp16.language;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.magicwerk.brownies.collections.GapList;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
        return this.getAsJsonElement(key).getAsString();
    }

    public String getAsString(String key, Object... params) {
        return MessageFormat.format(this.getAsJsonElement(key).getAsString(), params);
    }

    public Object getAsObject(String key) {
        return resourceBundle.getObject(key);
    }

    public JsonElement getAsJsonElement(String key) {
        return (JsonElement) this.getAsObject(key);
    }

    public List<JsonElement> getArrayAsJsonElement(String key) {
        List<JsonElement> elements = new GapList<>();

        JsonArray jsonArray = (JsonArray) this.getAsObject(key);

        jsonArray.forEach(elements::add);

        return elements;
    }

    public List<String> getArrayAsString(String key) {
        List<String> elements = new GapList<>();

        JsonArray jsonArray = (JsonArray) this.getAsObject(key);

        jsonArray.forEach(element -> elements.add(element.getAsString()));

        return elements;
    }

    public List<String> getArrayAsString(String key, Object... params) {
        List<String> elements = new GapList<>();

        JsonArray jsonArray = (JsonArray) this.getAsObject(key);

        jsonArray.forEach(element -> {
            elements.add(MessageFormat.format(element.getAsString(), params));
        });

        return elements;
    }

    public <T> List<T> getJsonArrayAsClass(String key, Class<T> tClass) {
        List<T> arrayList = new GapList<>();

        this.getArrayAsJsonElement(key).forEach(element -> {
            String tmp = resourceBundleControl.getGson().toJson(element);
            arrayList.add(resourceBundleControl.getGson().fromJson(tmp, tClass));
        });

        return arrayList;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public ResourceBundleControl getResourceBundleControl() {
        return resourceBundleControl;
    }
}
