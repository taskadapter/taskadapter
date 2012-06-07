package com.taskadapter.connector.redmine;

import java.util.ArrayList;
import java.util.List;

import org.redmine.ta.RedmineException;
import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Project;
import org.redmine.ta.beans.SavedQuery;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;

/**
 * Redmine data loaders.
 * 
 * @author maxkar
 * 
 */
public class RedmineLoaders {
	public static List<GProject> getProjects(WebServerInfo serverInfo)
			throws ValidationException {
		validate(serverInfo);

		RedmineManager mgr = RedmineManagerFactory
				.createRedmineManager(serverInfo);
		List<org.redmine.ta.beans.Project> rmProjects;
		try {
			rmProjects = mgr.getProjects();
		} catch (Exception e) {
			throw new RuntimeException(e.toString(), e);
		}

		return new RedmineProjectConverter().toGProjects(rmProjects);
	}

	private static void validate(WebServerInfo serverInfo)
			throws ValidationException {
		if ((serverInfo.getHost() == null) || (serverInfo.getHost().isEmpty())) {
			throw new ValidationException("Host URL is not set");
		}
	}

	/**
	 * Loads a project.
	 * 
	 * @param manager
	 *            manager.
	 * @param projectKey
	 *            project key.
	 * @return loaded project.
	 */
	public static Project loadProject(RedmineManager manager, String projectKey) {
		try {
			return manager.getProjectByKey(projectKey);
		} catch (RedmineException e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return null;
	}

	public static List<? extends NamedKeyedObject> loadData(
			WebServerInfo config, String projectKey)
			throws ValidationException, RedmineException {
		validate(config);
		RedmineManager mgr = RedmineManagerFactory.createRedmineManager(config);
		List<NamedKeyedObject> result = new ArrayList<NamedKeyedObject>();
		// get project id to filter saved queries
		Integer projectId = null;
		if (projectKey != null && projectKey.length() > 0) {
			Project project = mgr.getProjectByKey(projectKey);
			if (project != null) {
				projectId = project.getId();
			}
		}

		List<SavedQuery> savedQueries = mgr.getSavedQueries();
		// XXX refactor: we don't even need these IDs
		for (SavedQuery savedQuery : savedQueries) {
			Integer projectIdFromQuery = savedQuery.getProjectId();
			// we should add only common queries and queries which belongs to
			// selected project
			if (projectIdFromQuery == null || projectIdFromQuery == 0
					|| projectIdFromQuery.equals(projectId)) {
				result.add(new NamedKeyedObjectImpl(Integer.toString(savedQuery
						.getId()), savedQuery.getName()));
			}
		}
		return result;
	}
}
