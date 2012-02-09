package com.taskadapter.integrationtests;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.IDataConnectorTester;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineProjectConverter;
import com.taskadapter.model.GProject;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Project;

import java.util.Calendar;

public class RedmineTester implements IDataConnectorTester {
    @Override
    public GProject createRandomProject(ConnectorConfig config)
            throws Exception {
        Project junitTestProject = new Project();
        junitTestProject.setName("test project");
        junitTestProject.setIdentifier("test"
                + Calendar.getInstance().getTimeInMillis());
        Project createdProject = getRedmineManager(config).createProject(junitTestProject);
        return new RedmineProjectConverter().convertToGProject(createdProject);
    }

    @Override
    public void deleteProject(ConnectorConfig config, String projectKey)
            throws Exception {
        getRedmineManager(config).deleteProject(projectKey);
    }

    private static RedmineManager getRedmineManager(ConnectorConfig config) {
        RedmineConfig rmConfig = (RedmineConfig) config;
        WebServerInfo info = rmConfig.getServerInfo();
        return new RedmineManager(info.getHost(), info.getUserName(), info.getPassword());

    }
}
