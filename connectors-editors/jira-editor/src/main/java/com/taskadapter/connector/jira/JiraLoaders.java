package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Strings;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.GProject;

import java.util.List;

public class JiraLoaders {

    public static Priorities loadPriorities(WebConnectorSetup serverInfo) throws ConnectorException {
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

    static void validate(WebConnectorSetup setup) throws ServerURLNotSetException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
    }

}
