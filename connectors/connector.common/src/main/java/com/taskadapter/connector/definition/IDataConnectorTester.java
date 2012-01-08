package com.taskadapter.connector.definition;

import com.taskadapter.model.GProject;

public interface IDataConnectorTester {
	public GProject createRandomProject(ConnectorConfig config) throws Exception;
	public void deleteProject(ConnectorConfig config, String projectKey) throws Exception;
}
