package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.github.ConnectionFactory;
import com.taskadapter.connector.github.GithubProjectConverter;
import com.taskadapter.model.GProject;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.List;

public final class GithubLoaders {
    public static List<GProject> getProjects(WebServerInfo serverInfo) throws ServerURLNotSetException {
        validateServer(serverInfo);
        try {
            final String userName = serverInfo.getUserName();
            final ConnectionFactory connectionFactory = new ConnectionFactory(serverInfo);
            final RepositoryService repositoryService = connectionFactory.getRepositoryService();
            final List<Repository> repositories = repositoryService.getRepositories(userName);
            final GithubProjectConverter converter = new GithubProjectConverter();
            return converter.toGProjects(repositories);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private static void validateServer(WebServerInfo serverInfo) throws ServerURLNotSetException {
        if ((serverInfo.getHost() == null) || (serverInfo.getHost().isEmpty())) {
            throw new ServerURLNotSetException();
        }
    }

}
