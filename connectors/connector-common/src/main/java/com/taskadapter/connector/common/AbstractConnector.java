package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.model.GTask;

import java.util.List;

public abstract class AbstractConnector<T extends ConnectorConfig> implements Connector<T> {

    protected T config;

    protected AbstractConnector(T config) {
        super();
        this.config = config;
    }

    @Override
    public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor) {
        try {
            return getTaskSaver(config).saveData(tasks, monitor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getConfig() {
        return config;
    }

}
