package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.redmine.RedmineManagerFactory;
import com.taskadapter.connector.redmine.converter.RedmineProjectConverter;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.web.callbacks.DataProvider;

import java.util.List;

public class RedmineProjectListLoader implements DataProvider<List<? extends NamedKeyedObject>> {
    private final WebConnectorSetup setup;
    private final RedmineManager mgr;

    public RedmineProjectListLoader(WebConnectorSetup setup) {
        this.setup = setup;
        var httpClient = RedmineManagerFactory.createRedmineHttpClient(setup.getHost());
        mgr = RedmineManagerFactory.createRedmineManager(setup, httpClient);
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
        RedmineLoaders.validate(setup);
        try {
            var rmProjects = mgr.getProjectManager().getProjects();
            return new RedmineProjectConverter().toGProjects(rmProjects);
        } catch (RedmineException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
}
