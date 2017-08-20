package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

public class LoadQueriesElement {
    private JiraConfig config;
    private WebConnectorSetup webServerInfo;

    public LoadQueriesElement(JiraConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.webServerInfo = setup;
    }

    List<? extends NamedKeyedObject> loadQueries() throws ConnectorException {
        if (Strings.isNullOrEmpty(webServerInfo.host())) {
            throw new ServerURLNotSetException();
        }
        return new JiraConnector(config, webServerInfo).getFilters();
    }

}
