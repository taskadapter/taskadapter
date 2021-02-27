package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;

import java.util.List;

public class JiraQueryListLoader implements DataProvider<List<? extends NamedKeyedObject>> {
    private JiraConfig jiraConfig;
    private WebConnectorSetup setup;

    public JiraQueryListLoader(JiraConfig jiraConfig, WebConnectorSetup setup) {
        this.jiraConfig = jiraConfig;
        this.setup = setup;
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
        if (Strings.isNullOrEmpty(setup.getHost())) {
            throw new ServerURLNotSetException();
        }
        return new JiraConnector(jiraConfig, setup).getFilters();
    }
}
