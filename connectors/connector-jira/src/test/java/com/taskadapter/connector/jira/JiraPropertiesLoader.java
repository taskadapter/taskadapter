package com.taskadapter.connector.jira;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebConnectorSetup;

import java.util.Properties;

public class JiraPropertiesLoader {
    private static final Properties properties = PropertiesUtf8Loader.load("jira.properties");

    public static JiraConfig createTestConfig() {
        var config = new JiraConfig();
        config.setProjectKey(properties.getProperty("project.key"));
        config.setDefaultTaskType(properties.getProperty("defaultTaskType"));
        config.setDefaultIssueTypeForSubtasks(properties.getProperty("defaultSubTaskType"));
        return config;
    }

    public static WebConnectorSetup getTestServerInfo() {
        return WebConnectorSetup.apply(JiraConnector.ID, "label1", properties.getProperty("host"),
                properties.getProperty("login"), properties.getProperty("password"), false, "");
    }

    public static String getProjectKey() {
        return properties.getProperty("project.key");
    }
}
