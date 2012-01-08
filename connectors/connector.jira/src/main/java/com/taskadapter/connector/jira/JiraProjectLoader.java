package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemoteProject;
import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;

import java.util.Arrays;
import java.util.List;

public class JiraProjectLoader implements ProjectLoader {

	@Override
	public List<GProject> getProjects(WebServerInfo serverInfo) throws ValidationException {
		List<GProject> gProjects = null;
		try {
			JiraConnection connection = JiraConnectionFactory.createConnection(serverInfo);
			RemoteProject[] projects = connection.getProjects();
			gProjects = new JiraProjectConverter().toGProjects(Arrays.asList(projects));
		} catch (RemoteAuthenticationException e) {
			throw new JiraException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return gProjects;
	}

	public GProject getProject(WebServerInfo serverInfo, String projectKey) {
		GProject gProject = null;
		try {
			JiraConnection connection = JiraConnectionFactory.createConnection(serverInfo);
			RemoteProject project = connection.getProject(projectKey);
			gProject = new JiraProjectConverter().toGProject(project);
		} catch (RemoteAuthenticationException e) {
			throw new JiraException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return gProject;

	}
}
