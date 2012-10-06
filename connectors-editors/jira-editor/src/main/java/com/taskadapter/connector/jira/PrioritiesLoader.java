package com.taskadapter.connector.jira;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public class PrioritiesLoader {
    private JiraConfig config;

    public PrioritiesLoader(JiraConfig config) {
        this.config = config;
    }

    Priorities loadJiraPriorities() throws ValidationException, ConnectorException {
        if (!config.getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
        return JiraLoaders.loadPriorities(config.getServerInfo());
    }
}
