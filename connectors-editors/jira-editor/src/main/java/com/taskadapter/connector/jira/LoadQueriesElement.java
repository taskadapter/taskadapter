package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.NamedKeyedObject;

import java.util.List;

public class LoadQueriesElement {
    private JiraConfig config;
    private WebServerInfo webServerInfo;

    public LoadQueriesElement(JiraConfig config, WebServerInfo webServerInfo) {
        this.config = config;
        this.webServerInfo = webServerInfo;
    }

    List<? extends NamedKeyedObject> loadQueries() throws ConnectorException {
        if (!webServerInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
        return new JiraConnector(config, webServerInfo).getFilters();
    }

}
