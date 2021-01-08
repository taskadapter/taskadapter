package com.taskadapter.connector.redmine.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.callbacks.DataProvider;

public class PrioritiesLoader implements DataProvider<Priorities> {

    private WebConnectorSetup setup;

    public PrioritiesLoader(WebConnectorSetup setup) {
        this.setup = setup;
    }

    @Override
    public Priorities loadData() throws ConnectorException {
        if (Strings.isNullOrEmpty(setup.host())) {
            throw new ServerURLNotSetException();
        }
        return RedmineLoaders.loadPriorities(setup);
    }
}
