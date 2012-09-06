package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.Project;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.List;

public class JiraLoaders {

    public static Priorities loadPriorities(WebServerInfo serverInfo)
            throws ValidationException, ConnectorException {
        validate(serverInfo);
        final Priorities defaultPriorities = JiraConfig
                .createDefaultPriorities();
        final Priorities result = new Priorities();

        try {
            JiraConnection connection = JiraConnectionFactory
                    .createConnection(serverInfo);
            Iterable<Priority> priorities = connection.getPriorities();

            for (Priority priority : priorities) {
                result.setPriority(priority.getName(),
                        defaultPriorities.getPriorityByText(priority.getName()));
            }
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }

        return result;
    }

    public static List<GProject> loadProjects(WebServerInfo serverInfo)
            throws ValidationException, ConnectorException {
        validate(serverInfo);
        List<GProject> gProjects;
        try {
            JiraConnection connection = JiraConnectionFactory
                    .createConnection(serverInfo);

            Iterable<BasicProject> projects = connection.getProjects();
            gProjects = new JiraProjectConverter().toGProjects(projects);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
        }

        return gProjects;
    }

    public static GProject loadProject(WebServerInfo serverInfo,
                                       String projectKey) throws ValidationException, ConnectorException {
        GProject gProject;
        validate(serverInfo);
        try {
            JiraConnection connection = JiraConnectionFactory
                    .createConnection(serverInfo);

            Project project = connection.getProject(projectKey);
            gProject = new JiraProjectConverter().toGProject(project);
        } catch (RemoteException e) {
            throw JiraUtils.convertException(e);
        } catch (MalformedURLException e) {
            throw JiraUtils.convertException(e);
        } catch (URISyntaxException e) {
            throw JiraUtils.convertException(e);
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
