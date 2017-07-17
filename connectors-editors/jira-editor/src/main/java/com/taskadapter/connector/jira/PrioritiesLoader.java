package com.taskadapter.connector.jira;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;

public class PrioritiesLoader {
    private WebServerInfo webServerInfo;

    public PrioritiesLoader(WebServerInfo webServerInfo) {

        this.webServerInfo = webServerInfo;
    }

    Priorities loadJiraPriorities() throws ConnectorException {
        if (!webServerInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
        return JiraLoaders.loadPriorities(webServerInfo);
    }
}
