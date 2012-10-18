package com.taskadapter.web.service;

import com.taskadapter.web.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class CurrentVersionLoader {

    private static final String VERSION_PROPERTIES_FILE_NAME = "/version.properties";
    private final Logger logger = LoggerFactory.getLogger(CurrentVersionLoader.class);

    private AppInfo appInfo = new AppInfo();

    public CurrentVersionLoader() {
        loadCurrentVersion();
    }

    private void loadCurrentVersion() {
        Properties properties = new Properties();
        try {
            properties.load(CurrentVersionLoader.class.getResourceAsStream(VERSION_PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            logger.error("Error loading " + VERSION_PROPERTIES_FILE_NAME + ": " + e.getMessage(), e);
        }
        appInfo.setVersion(properties.getProperty("version"));
//        appInfo.setBuildDate(properties.getProperty("buildDate"));
    }

    public String getCurrentVersion() {
        return appInfo.getVersion();
    }
}
