package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;

public class PrioritiesLoader {
    private WebConnectorSetup webServerInfo;

    public PrioritiesLoader(WebConnectorSetup webServerInfo) {

        this.webServerInfo = webServerInfo;
    }

    Priorities loadJiraPriorities() throws ConnectorException {
        if (Strings.isNullOrEmpty(webServerInfo.host())) {
            throw new ServerURLNotSetException();
        }
        return JiraLoaders.loadPriorities(webServerInfo);
    }
}
