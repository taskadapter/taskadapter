package com.taskadapter.connector.mantis.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.mantis.MantisManagerFactory;
import com.taskadapter.connector.mantis.MantisProjectConverter;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;

import java.util.List;

public class MantisProjectsListLoader implements DataProvider<List<? extends NamedKeyedObject>> {
    private final WebConnectorSetup setup;

    public MantisProjectsListLoader(WebConnectorSetup setup) {
        this.setup = setup;
    }

    @Override
    public List<GProject> loadData() throws ConnectorException {
        validate(setup);
        var mgr = MantisManagerFactory.createMantisManager(setup);
        try {
            var mntProjects = mgr.getProjects();
            return new MantisProjectConverter().toGProjects(mntProjects);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private static void validate(WebConnectorSetup setup) throws ServerURLNotSetException {
        if (Strings.isNullOrEmpty(setup.getHost())) {
            throw new ServerURLNotSetException();
        }
    }
}
