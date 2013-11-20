package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JiraTestData {
    private static final String TEST_PROPERTIES = "jira.properties";
    private static Properties properties = new Properties();

    static {
        InputStream is = JiraTestData.class.getClassLoader().getResourceAsStream(TEST_PROPERTIES);
        if (is == null) {
            throw new RuntimeException("Can't find file " + TEST_PROPERTIES
                    + " in classpath.");
        }
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Can't load Jira test config: " + e.toString(), e);
        }

    }

    public JiraConfig createTestConfig() {
        JiraConfig config = new JiraConfig();
        config.setServerInfo(getTestServerInfo());
        config.setProjectKey(properties.getProperty("project.key"));
        config.setDefaultTaskType(properties.getProperty("defaultTaskType"));
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
