package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.IDataConnectorTester;
import com.taskadapter.model.GProject;

public class JiraTester implements IDataConnectorTester {
    @Override
    public GProject createRandomProject(ConnectorConfig config)
            throws Exception {
//		Project junitTestProject = new Project();
//		junitTestProject.setName("test project");
//		junitTestProject.setIdentifier("test"
//				+ Calendar.getInstance().getTimeInMillis());
//		Project createdProject = getRedmineManager(config).createProject(junitTestProject);
//		return RedmineRESTAPIConnector.convertToGProject(createdProject);
        return null;
    }

    @Override
    public void deleteProject(ConnectorConfig config, String projectKey)
            throws Exception {
//		getRedmineManager(config).deleteProject(projectKey);
    }

//	private static RedmineManager getRedmineManager(ConnectorConfig config) {
//		RedmineConfig rmConfig = (RedmineConfig) config;
//		RedmineManager mgr = new RedmineManager(rmConfig.getHost(),
//				rmConfig.getUserName(), rmConfig.getPassword());
//		return mgr;
//
//	}
}
