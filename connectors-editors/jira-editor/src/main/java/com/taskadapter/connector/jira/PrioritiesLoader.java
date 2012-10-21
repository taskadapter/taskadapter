package com.taskadapter.connector.jira;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;

public class PrioritiesLoader {
    private JiraConfig config;

    public PrioritiesLoader(JiraConfig config) {
        this.config = config;
    }

    Priorities loadJiraPriorities() throws ConnectorException {
        if (!config.getServerInfo().isHostSet()) {
            throw new ServerURLNotSetException();
        }
        return JiraLoaders.loadPriorities(config.getServerInfo());
    }
}
