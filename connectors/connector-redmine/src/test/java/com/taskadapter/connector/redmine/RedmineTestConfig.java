package com.taskadapter.connector.redmine;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.Properties;

class RedmineTestConfig {
    private static final String TEST_PROPERTIES = "redmine.properties";

    private static final Properties properties = PropertiesUtf8Loader.load(TEST_PROPERTIES);

    static RedmineConfig getRedmineTestConfig() {
        WebServerInfo rmInfo = new WebServerInfo(properties.getProperty("uri"), "", "");
        rmInfo.setApiKey(properties.getProperty("apikey"));
        rmInfo.setUseAPIKeyInsteadOfLoginPassword(true);
        RedmineConfig redmineConfig = new RedmineConfig();
        redmineConfig.setServerInfo(rmInfo);
        redmineConfig.setProjectKey(properties.getProperty("project.key"));
        return redmineConfig;
    }
}
