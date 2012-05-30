package com.taskadapter.connector.definition;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.model.GTask;

import java.util.List;

public interface Connector<T extends ConnectorConfig> {

	/**
	 * Loads a list of tasks. Order of loaded tasks is not specified and may
	 * depend on implementation. To get tasks in a specific order, use
	 * {@link ConnectorUtils} methods.
	 * 
	 * @param monitor
	 *            can't be null. See
	 *            {@link ProgressMonitorUtils#getDummyMonitor()} if you don't
	 *            want any monitoring.
	 * @throws Exception
	 *             some other exceptions the connector might throw
	 */
    public List<GTask> loadData(ProgressMonitor monitor);

    /**
     * Loads one task by it's key.
     * @param key task key.
     * @return loaded task.
     */
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
