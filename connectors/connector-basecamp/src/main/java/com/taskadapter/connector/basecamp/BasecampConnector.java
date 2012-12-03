package com.taskadapter.connector.basecamp;

import java.util.List;
import java.util.Map;

import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;

final class BasecampConnector implements Connector<BasecampConfig> {

    private final BasecampConfig config;
    private final ObjectAPIFactory factory;

    BasecampConnector(BasecampConfig config, ObjectAPIFactory factory) {
        super();
        this.config = config;
        this.factory = factory;
    }

    @Override
    public List<GTask> loadData(Mappings mappings, ProgressMonitor monitor)
            throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings)
            throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor,
            Mappings mappings) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateRemoteIDs(Map<Integer, String> remoteIds,
            ProgressMonitor monitor, Mappings mappings)
            throws ConnectorException {
        throw new UnsupportedConnectorOperation("remote-ids");
    }

}
