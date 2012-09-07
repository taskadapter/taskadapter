package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.WebServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// TODO duplicate of Jira code
public class GithubTestData {
    private static final String TEST_PROPERTIES = "github.test.properties";
    private static Properties properties = new Properties();

    static {
        InputStream is = GithubTestData.class.getClassLoader().getResourceAsStream(TEST_PROPERTIES);
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

    public GithubConfig createTestConfig() {
        GithubConfig config = new GithubConfig();
        config.setServerInfo(getTestServerInfo());
        config.setProjectKey(properties.getProperty("repository"));
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
