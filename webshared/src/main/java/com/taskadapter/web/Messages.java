package com.taskadapter.web;

import com.taskadapter.util.MyIOUtils;

import java.io.IOException;
import java.util.Properties;

public class Messages {
    // TODO use proper i18n instead of this primitive temporary solution
    private static final String HELP_EN_FILE_PROPERTIES = "help_en.properties";
    private static Properties properties;

    static {
        loadMessagesFromClasspath();
    }

    private static void loadMessagesFromClasspath() {
        properties = new Properties();
        try {
            properties.load(MyIOUtils.getResourceAsStream(HELP_EN_FILE_PROPERTIES));
        } catch (IOException e) {
            throw new RuntimeException("Can't load file " + HELP_EN_FILE_PROPERTIES);
        }
    }

    public static String getMessageDefaultLocale(String id) {
        return properties.getProperty(id);
    }
}
