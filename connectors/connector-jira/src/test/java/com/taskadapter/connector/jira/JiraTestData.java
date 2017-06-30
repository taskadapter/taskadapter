package com.taskadapter.connector.jira;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebServerInfo;

import java.util.Properties;

public class JiraTestData {
    private static final String TEST_PROPERTIES = "jira.properties";
    private static Properties properties = PropertiesUtf8Loader.load(TEST_PROPERTIES);

    public JiraConfig createTestConfig() {
        JiraConfig config = new JiraConfig();
        config.setServerInfo(getTestServerInfo());
        config.setProjectKey(properties.getProperty("project.key"));
        config.setDefaultTaskType(properties.getProperty("defaultTaskType"));
        config.setDefaultIssueTypeForSubtasks(properties.getProperty("defaultSubTaskType"));
        return config;
    }

    public WebServerInfo getTestServerInfo() {
        return new WebServerInfo(properties.getProperty("host"),
                properties.getProperty("login"),
                properties.getProperty("password"));
    }

    public String getProjectKey() {
        return properties.getProperty("project.key");
    }
}
