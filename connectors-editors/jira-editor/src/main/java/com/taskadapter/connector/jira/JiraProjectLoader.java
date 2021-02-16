package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.GProject;
import com.taskadapter.web.callbacks.DataProvider;

public class JiraProjectLoader implements DataProvider<GProject> {
    private JiraConfig jiraConfig;
    private WebConnectorSetup setup;

    public JiraProjectLoader(JiraConfig jiraConfig, WebConnectorSetup setup) {
        this.jiraConfig = jiraConfig;
        this.setup = setup;
    }

    @Override
    public GProject loadData() throws ConnectorException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
        if (jiraConfig.getProjectKey() == null || jiraConfig.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        return loadProject(setup, jiraConfig.getProjectKey());
    }

    private static GProject loadProject(WebConnectorSetup setup, String projectKey) throws ConnectorException {
        JiraLoaders.validate(setup);
        try (JiraRestClient client = JiraConnectionFactory.createClient(setup)) {
            var promise = client.getProjectClient().getProject(projectKey);
            var project = promise.claim();
            return new JiraProjectConverter().toGProject(project);
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
    }
}
