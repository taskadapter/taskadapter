package com.taskadapter.connector.github;

import java.util.List;

import org.eclipse.egit.github.core.Repository;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;

public class GithubLoaders {
	public static List<GProject> getProjects(WebServerInfo serverInfo)
			throws ValidationException {
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

	private static void validateServer(WebServerInfo serverInfo)
			throws ValidationException {
		if ((serverInfo.getHost() == null) || (serverInfo.getHost().isEmpty())) {
			throw new ValidationException("Host URL is not set");
		}
	}

}
