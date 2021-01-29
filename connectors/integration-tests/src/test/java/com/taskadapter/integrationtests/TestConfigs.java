package com.taskadapter.integrationtests;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.jira.JiraConfig;
import com.taskadapter.connector.jira.JiraConnector;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import scala.Option;

import java.util.Properties;

public class TestConfigs {
    private static Properties properties = PropertiesUtf8Loader.load("redmine_test_data.properties");
    private static Properties jiraProperties = PropertiesUtf8Loader.load("jira.properties");

    public static RedmineConfig getRedmineConfig() {
        RedmineConfig redmineConfig = new RedmineConfig();
        redmineConfig.setProjectKey(properties.getProperty("project.key"));
        return redmineConfig;
    }

    public static WebConnectorSetup getRedmineSetup() {
        return new WebConnectorSetup(RedmineConnector.ID(), Option.empty(),
                "label1", properties.getProperty("uri"), "", "",
                true, properties.getProperty("apikey"));
    }

    public static JiraConfig getJiraConfig() {
        JiraConfig config = new JiraConfig();
        config.setProjectKey(jiraProperties.getProperty("project.key"));
        config.setQueryId(Long.parseLong(jiraProperties.getProperty("queryId")));
        return config;
    }

    public static WebConnectorSetup getJiraSetup() {
        return new WebConnectorSetup(JiraConnector.ID(),
                Option.empty(),
                "label1", jiraProperties.getProperty("host"),
                jiraProperties.getProperty("login"), jiraProperties.getProperty("password"), false, "");
    }
}
