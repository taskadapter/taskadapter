package com.taskadapter.connector.redmine;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebConnectorSetup;

import java.util.Properties;

public class RedmineTestConfig {
    private static final String TEST_PROPERTIES = "redmine.properties";
    private static final Properties properties = PropertiesUtf8Loader.load(TEST_PROPERTIES);

    public static RedmineConfig getRedmineTestConfig() {
        var redmineConfig = new RedmineConfig();
        redmineConfig.setProjectKey(properties.getProperty("project.key"));
        return redmineConfig;
    }

    public static WebConnectorSetup getRedmineServerInfo() {
        return WebConnectorSetup.apply(RedmineConnector.ID, "label1", properties.getProperty("uri"), "", "",
                true, properties.getProperty("apikey"));
    }
}
