package com.cometproject.server.config;

import com.cometproject.server.storage.queries.config.LocaleDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


public class Locale {
    /**
     * Logging for locale object
     */
    private static final Logger log = LogManager.getLogger(Locale.class.getName());

    /**
     * Store locale in memory
     */
    private static Map<String, String> locale;

    /**
     * Initialize the locale
     */
    public static void initialize() {
        reload();
    }

    /**
     * Load locale from the database
     */
    public static void reload() {
        if (locale != null)
            locale.clear();

        locale = LocaleDao.getAll();
        log.info("Loaded " + locale.size() + " locale strings");
    }

    /**
     * Get a locale string by the key
     *
     * @param key Retrieve from the locale by the key
     * @return String from the locale
     */
    public static String get(String key) {
        return locale.getOrDefault(key, key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        return locale.getOrDefault(key, defaultValue);
    }

    public static Map<String, String> getAll() {
        return locale;
    }
}
