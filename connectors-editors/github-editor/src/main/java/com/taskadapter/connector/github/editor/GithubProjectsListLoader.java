package com.taskadapter.connector.github.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.github.ConnectionFactory;
import com.taskadapter.connector.github.GithubProjectConverter;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;

import java.util.List;

public class GithubProjectsListLoader implements DataProvider<List<? extends NamedKeyedObject>> {
    private WebConnectorSetup setup;

    public GithubProjectsListLoader(WebConnectorSetup setup) {
        this.setup = setup;
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
        validateServer(setup);
        try {
            var connectionFactory = new ConnectionFactory(setup);
            var repositoryService = connectionFactory.getRepositoryService();
            var repositories = repositoryService.getRepositories(setup.getUserName());
            var converter = new GithubProjectConverter();
            return converter.toGProjects(repositories);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private void validateServer(WebConnectorSetup setup) throws ServerURLNotSetException {
        if (Strings.isNullOrEmpty(setup.getHost())) {
            throw new ServerURLNotSetException();
        }
    }
}
