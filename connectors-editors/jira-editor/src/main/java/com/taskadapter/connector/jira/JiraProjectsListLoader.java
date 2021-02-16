package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;

import java.util.List;

public class JiraProjectsListLoader implements DataProvider<List<? extends NamedKeyedObject>> {
    private WebConnectorSetup setup;

    public JiraProjectsListLoader(WebConnectorSetup setup) {
        this.setup = setup;
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
        JiraLoaders.validate(setup);
        try (JiraRestClient client = JiraConnectionFactory.createClient(setup)) {
            var promise = client.getProjectClient().getAllProjects();
            var projects = promise.claim();
            return new JiraProjectConverter().toGProjects(projects);
        } catch (Exception e) {
            throw JiraUtils.convertException(e);
        }
    }
}
