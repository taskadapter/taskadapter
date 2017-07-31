package com.taskadapter.connector.redmine.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineExceptions;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.connector.redmine.converter.RedmineProjectConverter;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.SavedQuery;
import com.taskadapter.redmineapi.bean.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Redmine data loaders.
 */
public class RedmineLoaders {

    private static final Logger logger = LoggerFactory.getLogger(RedmineLoaders.class);

    public static List<GProject> getProjects(WebConnectorSetup setup)
            throws ServerURLNotSetException {
        validate(setup);

        RedmineManager mgr = RedmineManagerFactory.createRedmineManager(setup);
        List<com.taskadapter.redmineapi.bean.Project> rmProjects;
        try {
            rmProjects = mgr.getProjectManager().getProjects();
        } catch (RedmineException e) {
            throw new RuntimeException(e.toString(), e);
        }

        return new RedmineProjectConverter().toGProjects(rmProjects);
    }

    private static void validate(WebConnectorSetup setup) throws ServerURLNotSetException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
    }

    /**
     * Loads a project.
     *
     * @param manager    manager.
     * @param projectKey project key.
     * @return loaded project.
     */
    public static Project loadProject(ProjectManager manager, String projectKey) {
        try {
            return manager.getProjectByKey(projectKey);
        } catch (RedmineException e) {
            logger.error("Error loading redmine project with key '" + projectKey + "'. " + e.getMessage(), e);
        }
        return null;
    }

    public static List<? extends NamedKeyedObject> loadData(WebConnectorSetup setup, String projectKey) throws BadConfigException, RedmineException {
        validate(setup);
        RedmineManager mgr = RedmineManagerFactory.createRedmineManager(setup);
        List<NamedKeyedObject> result = new ArrayList<>();
        // get project id to filter saved queries
        Integer projectId = null;
        if (projectKey != null && projectKey.length() > 0) {
            Project project = mgr.getProjectManager().getProjectByKey(projectKey);
            if (project != null) {
                projectId = project.getId();
            }
        }

        List<SavedQuery> savedQueries = mgr.getIssueManager().getSavedQueries();
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

    public static List<? extends NamedKeyedObject> loadTrackers(RedmineConfig config, WebConnectorSetup setup) throws ConnectorException {
        validate(setup);
        RedmineManager redmineManager = RedmineManagerFactory.createRedmineManager(setup);
        Project project;
        String projectKey = config.getProjectKey();
        try {
            project = redmineManager.getProjectManager().getProjectByKey(projectKey);
        } catch (RedmineException e) {
            throw new ConnectorException("Some Redmine problem when loading project by key " + projectKey);
        }

        Collection<Tracker> trackers = project.getTrackers();
        List<NamedKeyedObject> result = new ArrayList<>(trackers.size());

        // XXX refactor: we don't even need these IDs
        for (Tracker tracker : trackers) {
            result.add(new NamedKeyedObjectImpl(Integer.toString(tracker
                    .getId()), tracker.getName()));
        }
        return result;
    }
    
    public static Priorities loadPriorities(WebConnectorSetup setup) throws ConnectorException {
        validate(setup);
        final Priorities defaultPriorities = RedmineConfig
                .generateDefaultPriorities();
        final RedmineManager mgr = RedmineManagerFactory
                .createRedmineManager(setup);
        final Priorities result = new Priorities();
        try {
            for (IssuePriority prio : mgr.getIssueManager().getIssuePriorities()) {
                result.setPriority(prio.getName(),
                        defaultPriorities.getPriorityByText(prio.getName()));
            }
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
        return result;
    }
}
