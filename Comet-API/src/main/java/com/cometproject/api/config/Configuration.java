package com.cometproject.api.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;


public class Configuration extends Properties {
    /**
     * The configuration logger
     */
    private static final Logger log = LogManager.getLogger(Configuration.class.getName());

    private static Configuration configuration;

    /**
     * Initialize the configuration object
     * This configuration will be loaded from the *.properties files in /com.cometproject.networking.api.config
     *
     * @param file The name of the com.cometproject.networking.api.config file
     */
    public Configuration(String file) {
        super();

        try {
            Reader stream = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

            this.load(stream);
            stream.close();
        } catch (Exception e) {
            log.error("Failed to fetch the server configuration (" + file + ")");
            System.exit(1);
        }
    }

    /**
     * Override configuration
     *
     * @param config The com.cometproject.networking.api.config strings which you want to override
     */
    public void override(Map<String, String> config) {
        for (Map.Entry<String, String> configOverride : config.entrySet()) {
            this.remove(configOverride.getKey());
            this.put(configOverride.getKey(), configOverride.getValue());
        }
    }

    /**
     * Get a string from the configuration
     *
     * @param key Retrieve a value from the com.cometproject.networking.api.config by the key
     * @return Value from the configuration
     */
    public String get(String key) {
        return this.getProperty(key);
    }

    public String get(String key, String fallback) {
        if (this.containsKey(key)) {
            return this.get(key);
        }

        return fallback;
    }

    public static Configuration currentConfig() {
        return configuration;
    }

    public static void setConfiguration(Configuration conf) {
        configuration = conf;
    }
}
