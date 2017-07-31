package com.taskadapter.connector.redmine;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebConnectorSetup;

import java.util.Properties;

class RedmineTestConfig {
    private static final String TEST_PROPERTIES = "redmine.properties";

    private static final Properties properties = PropertiesUtf8Loader.load(TEST_PROPERTIES);

    static RedmineConfig getRedmineTestConfig() {
        RedmineConfig redmineConfig = new RedmineConfig();
        redmineConfig.setProjectKey(properties.getProperty("project.key"));
        return redmineConfig;
    }

    static WebConnectorSetup getRedmineServerInfo() {
        return new WebConnectorSetup(RedmineConnector.ID(), "label1", properties.getProperty("uri"), "", "",
                true,properties.getProperty("apikey"));
    }
}
