package com.taskadapter.connector.github.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.github.ConnectionFactory;
import com.taskadapter.connector.github.GithubProjectConverter;
import com.taskadapter.model.GProject;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.List;

public final class GithubLoaders {
    public static List<GProject> getProjects(WebConnectorSetup setup) throws ServerURLNotSetException {
        validateServer(setup);
        try {
            final ConnectionFactory connectionFactory = new ConnectionFactory(setup);
            final RepositoryService repositoryService = connectionFactory.getRepositoryService();
            final List<Repository> repositories = repositoryService.getRepositories(setup.userName());
            final GithubProjectConverter converter = new GithubProjectConverter();
            return converter.toGProjects(repositories);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private static void validateServer(WebConnectorSetup setup) throws ServerURLNotSetException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
    }

}
