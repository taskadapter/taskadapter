package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;

public class PrioritiesLoader {
    private RedmineConfig config;

    public PrioritiesLoader(RedmineConfig config) {
        this.config = config;
    }

    Priorities loadPriorities() throws ConnectorException {
        if (!config.getServerInfo().isHostSet()) {
            throw new ServerURLNotSetException();
        }
        return RedmineLoaders.loadPriorities(config.getServerInfo());
    }
}
