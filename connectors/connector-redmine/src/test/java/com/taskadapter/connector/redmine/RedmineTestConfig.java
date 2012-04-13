package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedmineTestConfig {
    private static final String TEST_PROPERTIES = "redmine.properties";

    private static final Properties properties = new Properties();

    static {
        InputStream is = RedmineTestConfig.class.getClassLoader().getResourceAsStream(
                TEST_PROPERTIES);
        if (is == null) {
            throw new RuntimeException(
                    "Can't find file "
                            + TEST_PROPERTIES
                            + " in classpath. Please create it using one of the templates");
        }
        try {
            properties.load(is);//MyIOUtils.getResourceAsStream(TEST_PROPERTIES));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getURI() {
        return properties.getProperty("uri");
    }

    public static String getUserLogin() {
        return properties.getProperty("user");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

    public static String getApiKey() {
        return properties.getProperty("apikey");
    }

    public static String getParam(String key) {
        return properties.getProperty(key);
    }

    public static RedmineConfig getRedmineTestConfig() {
        WebServerInfo rmInfo = new WebServerInfo(RedmineTestConfig.getURI(), RedmineTestConfig.getUserLogin(), RedmineTestConfig.getPassword());
        RedmineConfig redmineConfig = new RedmineConfig();
        redmineConfig.setServerInfo(rmInfo);
        redmineConfig.setProjectKey(properties.getProperty("project.key"));
        return redmineConfig;
    }
}
