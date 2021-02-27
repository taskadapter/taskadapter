package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisManagerFactory;
import com.taskadapter.connector.mantis.MantisUtils;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.callbacks.DataProvider;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MantisQueryListLoader implements DataProvider<List<? extends NamedKeyedObject>> {

    private final MantisConfig config;
    private final WebConnectorSetup setup;

    public MantisQueryListLoader(MantisConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    @Override
    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
        var mgr = MantisManagerFactory.createMantisManager(setup);
        try {
            var projectKey = config.getProjectKey() == null ? null : new BigInteger(config.getProjectKey());
            var filters = mgr.getFilters(projectKey);
            return Arrays.stream(filters)
                    .map(filter -> new NamedKeyedObjectImpl(filter.getId().toString(), filter.getName()))
                    .collect(Collectors.toList());
        } catch (RemoteException e) {
            throw MantisUtils.convertException(e);
        }
    }
}
