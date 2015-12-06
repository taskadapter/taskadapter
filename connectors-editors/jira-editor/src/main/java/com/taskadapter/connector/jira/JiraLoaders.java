package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.GProject;

import java.util.List;

public class JiraLoaders {

    public static Priorities loadPriorities(WebServerInfo serverInfo) throws ConnectorException {
        validate(serverInfo);
        final Priorities defaultPriorities = JiraConfig.createDefaultPriorities();
        final Priorities result = new Priorities();

        try(JiraRestClient client = JiraConnectionFactory.createClient(serverInfo)) {
            Promise<Iterable<Priority>> prioritiesPromise = client.getMetadataClient().getPriorities();
            final Iterable<Priority> priorities = prioritiesPromise.claim();
            for (Priority priority : priorities) {
                result.setPriority(priority.getName(),
                        defaultPriorities.getPriorityByText(priority.getName()));
            }
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }

        return result;
    }

    // this is used through reflection in the UI
    public static List<GProject> loadProjects(WebServerInfo serverInfo) throws ConnectorException {
        validate(serverInfo);
        List<GProject> gProjects;
        try(JiraRestClient client = JiraConnectionFactory.createClient(serverInfo)) {
            Promise<Iterable<BasicProject>> promise = client.getProjectClient().getAllProjects();
            final Iterable<BasicProject> projects = promise.claim();
            gProjects = new JiraProjectConverter().toGProjects(projects);
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
        return gProjects;
    }

    public static GProject loadProject(WebServerInfo serverInfo, String projectKey) throws ConnectorException {
        GProject gProject;
        validate(serverInfo);
        try(JiraRestClient client = JiraConnectionFactory.createClient(serverInfo)) {
            Promise<Project> promise = client.getProjectClient().getProject(projectKey);
            final Project project = promise.claim();
            gProject = new JiraProjectConverter().toGProject(project);
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
        return gProject;
    }

    private static void validate(WebServerInfo serverInfo) throws ServerURLNotSetException {
        if ((serverInfo.getHost() == null) || (serverInfo.getHost().isEmpty())) {
            throw new ServerURLNotSetException();
        }
    }

}
