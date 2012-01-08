package com.taskadapter.connector.github;

import com.taskadapter.connector.common.ProjectLoader;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;
import org.eclipse.egit.github.core.Repository;

import java.util.List;

/**
 * GithubProjectLoader load github repositories
 */
public class GithubProjectLoader implements ProjectLoader {
    public List<GProject> getProjects(WebServerInfo serverInfo) throws ValidationException {
        try {
            List<Repository> repositories = new ConnectionFactory(serverInfo).getRepositoryService().getRepositories(serverInfo.getUserName());
        	return new GithubProjectConverter().toGProjects(repositories);
		} catch (Exception e) {
			throw new RuntimeException(e.toString(), e);
		}
    }
}
