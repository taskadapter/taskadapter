package com.taskadapter.webui.service;

import com.taskadapter.connector.PropertiesUtf8Loader;

import java.util.Optional;
import java.util.Properties;

public class TaPropertiesLoader {

    private static final String PROPERTIES_FILE_NAME = "ta.properties";
    private static final Properties properties = PropertiesUtf8Loader.load(PROPERTIES_FILE_NAME);

    public static String getCurrentAppVersion() {
        return properties.getProperty("version");
    }

    public static Optional<String> getRollbarApiToken() {
        return Optional.ofNullable(properties.getProperty("rollbarApiToken"));
    }

    public static String getUpdateAppUrl() {
        return properties.getProperty("update_site_url");
    }
}
