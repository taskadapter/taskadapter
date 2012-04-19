package com.taskadapter.connector.definition;

import java.util.List;

import com.taskadapter.model.GTask;

public interface Connector<T extends ConnectorConfig> {

    /**
     * @throws ValidationException if the configuration object is invalid
     * @throws Exception           some other exceptions the connector might throw
     */
    public List<GTask> loadData(ProgressMonitor monitor);

    public GTask loadTaskByKey(String key);

    /**
     * @param tasks
     * @param monitor the monitor is already started and will be marked by Task Adapter core as "done" after the operation is completed.
     *                connectors only need to invoke monitor.worked(1) when a task is processed.
     * @return
     */
    public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor);

    /**
     * is called after data was exported from this connector and we got some new "remote IDs", which need to
     * be saved in this connector
     *
     * @param monitor ProgressMonitor, can be NULL
     */
    public void updateRemoteIDs(ConnectorConfig sourceConfig,
                                SyncResult actualSaveResult, com.taskadapter.connector.definition.ProgressMonitor monitor);

    public T getConfig();

    public Descriptor getDescriptor();
}
