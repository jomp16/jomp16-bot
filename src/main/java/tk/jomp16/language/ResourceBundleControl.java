package tk.jomp16.language;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ResourceBundleControl extends ResourceBundle.Control {
    private final String CHARSET = "UTF-8";
    private final String FORMAT_JSON = "json";
    private final List<String> FORMATS = new ArrayList<>();
    private String CURRENT_ELEMENT;
    private Gson gson;

    public ResourceBundleControl() {
        FORMATS.addAll(FORMAT_DEFAULT);
        FORMATS.add(FORMAT_JSON);
        gson = new Gson();
    }

    @Override
    public List<String> getFormats(String baseName) {
        return FORMATS;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        this.CURRENT_ELEMENT = format;

        if (!FORMAT_JSON.equals(format)) {
            return super.newBundle(baseName, locale, format, loader, reload);
        }

        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, format);

        InputStream is = loader.getResourceAsStream(resourceName);

        if (is == null) {
            return null;
        }

        JsonObject jsonObject;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET))) {
            jsonObject = gson.fromJson(br, JsonObject.class);
        }

        JSONResourceBundle rb = new JSONResourceBundle();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            rb.put(entry.getKey(), entry.getValue());
        }

        return rb;
    }

    public boolean isJSON() {
        return CURRENT_ELEMENT == null || FORMAT_JSON.equals(CURRENT_ELEMENT);
    }

    public Gson getGson() {
        return gson;
    }

    private static class JSONResourceBundle extends ResourceBundle {
        private Map<String, JsonElement> data = new HashMap<>();

        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(data.keySet());
        }

        @Override
        protected Object handleGetObject(String key) {
            return data.get(key);
        }

        public void put(String key, JsonElement value) {
            data.put(key, value);
        }
    }
}
