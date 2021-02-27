package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.callbacks.DataProvider;

public class PrioritiesLoader implements DataProvider<Priorities> {
    private WebConnectorSetup webServerInfo;

    public PrioritiesLoader(WebConnectorSetup webServerInfo) {
        this.webServerInfo = webServerInfo;
    }

    @Override
    public Priorities loadData() throws ConnectorException {
        if (Strings.isNullOrEmpty(webServerInfo.getHost())) {
            throw new ServerURLNotSetException();
        }
        return JiraLoaders.loadPriorities(webServerInfo);
    }
}
