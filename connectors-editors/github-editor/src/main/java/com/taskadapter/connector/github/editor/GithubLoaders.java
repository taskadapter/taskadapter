package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.github.ConnectionFactory;
import com.taskadapter.connector.github.GithubProjectConverter;
import com.taskadapter.model.GProject;
import org.eclipse.egit.github.core.Repository;

import java.util.List;

public class GithubLoaders {
    public static List<GProject> getProjects(WebServerInfo serverInfo)
            throws ServerURLNotSetException {
        validateServer(serverInfo);
        try {
            List<Repository> repositories = new ConnectionFactory(serverInfo)
                    .getRepositoryService().getRepositories(
                            serverInfo.getUserName());
            return new GithubProjectConverter().toGProjects(repositories);
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
