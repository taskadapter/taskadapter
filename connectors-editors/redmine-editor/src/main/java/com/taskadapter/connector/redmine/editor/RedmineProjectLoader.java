package com.taskadapter.connector.redmine.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.connector.redmine.converter.RedmineProjectConverter;
import com.taskadapter.model.GProject;
import com.taskadapter.redmineapi.ProjectManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.web.callbacks.DataProvider;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RedmineProjectLoader implements DataProvider<GProject> {
    private static final Logger logger = LoggerFactory.getLogger(RedmineProjectLoader.class);

    private final RedmineConfig config;
    private final WebConnectorSetup setup;
    private final HttpClient httpClient;

    public RedmineProjectLoader(RedmineConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;

        httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.host());

    }

    @Override
    public GProject loadData() throws ConnectorException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
        var redmineManager = RedmineManagerFactory.createRedmineManager(setup, httpClient);
        var project = loadProject(redmineManager.getProjectManager(), config.getProjectKey());
        return RedmineProjectConverter.convertToGProject(
                project.orElseThrow(() ->
                        new ConnectorException("Cannot find Redmine project with key '" + config.getProjectKey() + "'")));
    }

    private static Optional<Project> loadProject(ProjectManager manager, String projectKey) {
        try {
            return Optional.of(manager.getProjectByKey(projectKey));
        } catch (RedmineException e) {
            logger.error("Error loading redmine project with key '" + projectKey + "'. " + e.getMessage());
        }
        return Optional.empty();
    }
}
