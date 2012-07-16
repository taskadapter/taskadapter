package com.taskadapter.connector.jira;

import java.util.Arrays;
import java.util.List;

import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.atlassian.jira.rpc.soap.client.RemoteProject;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;

public class JiraLoaders {

	public static Priorities loadPriorities(WebServerInfo serverInfo)
			throws ValidationException {
		validate(serverInfo);
		final Priorities defaultPriorities = JiraConfig
				.createDefaultPriorities();
		final Priorities result = new Priorities();

		try {
			JiraConnection connection = JiraConnectionFactory
					.createConnection(serverInfo);
			RemotePriority[] priorities = connection.getPriorities();

			for (RemotePriority priority : priorities) {
				result.setPriority(priority.getName(),
						defaultPriorities.getPriorityByText(priority.getName()));
			}
		} catch (RemoteAuthenticationException e) {
			throw new RuntimeException(e.getFaultString());
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

		return result;
	}

	public static List<GProject> loadProjects(WebServerInfo serverInfo)
			throws ValidationException {
		validate(serverInfo);
		List<GProject> gProjects;
		try {
			JiraConnection connection = JiraConnectionFactory
					.createConnection(serverInfo);
			//RemoteProject[] projects = connection.getProjects();
            //gProjects = new JiraProjectConverter().toGProjects(Arrays
                    //.asList(projects));

            Iterable<BasicProject> projects = connection.getProjects();
            gProjects = new JiraProjectConverter().toGProjects(projects);

		} catch (RemoteAuthenticationException e) {
			throw new JiraException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return gProjects;
	}

	public static GProject loadProject(WebServerInfo serverInfo,
			String projectKey) throws ValidationException {
		GProject gProject;
		validate(serverInfo);
		try {
			JiraConnection connection = JiraConnectionFactory
					.createConnection(serverInfo);
			//RemoteProject project = connection.getProject(projectKey);
            Project project = connection.getProject(projectKey);
			gProject = new JiraProjectConverter().toGProject(project);
		} catch (RemoteAuthenticationException e) {
			throw new JiraException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return gProject;

	}

	private static void validate(WebServerInfo serverInfo)
			throws ValidationException {
		if ((serverInfo.getHost() == null) || (serverInfo.getHost().isEmpty())) {
			throw new ValidationException("Host URL is not set");
		}
	}

}
