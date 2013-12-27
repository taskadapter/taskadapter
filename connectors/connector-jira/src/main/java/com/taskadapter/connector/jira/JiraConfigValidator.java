package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;

class JiraConfigValidator {
    static void validateServerURLSet(JiraConfig config) throws ServerURLNotSetException {
        if (!config.getServerInfo().isHostSet()) {
            throw new ServerURLNotSetException();
        }
    }
}
