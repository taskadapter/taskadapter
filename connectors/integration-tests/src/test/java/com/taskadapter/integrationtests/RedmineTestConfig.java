package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.redmine.RedmineConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// TODO Alexey: this file was copied from Redmine Connector / tests / Config class
// during Eclipse -> web UI & Maven migration. refactor this later, remove duplication
public class RedmineTestConfig {
    private static final String TEST_PROPERTIES = "redmine_test_data.properties";

    private static Properties properties = new Properties();

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
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Can't load properties file: " + e, e);
        }
    }

    public static RedmineConfig getRedmineTestConfig() {
        WebServerInfo rmInfo = new WebServerInfo(properties.getProperty("uri"), "", "");
        rmInfo.setApiKey(properties.getProperty("apikey"));
        rmInfo.setUseAPIKeyInsteadOfLoginPassword(true);
        RedmineConfig redmineConfig = new RedmineConfig();
        redmineConfig.setServerInfo(rmInfo);
        return redmineConfig;
    }
}
