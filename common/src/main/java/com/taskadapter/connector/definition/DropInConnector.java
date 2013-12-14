package com.taskadapter.connector.definition;

import java.io.File;
import java.util.List;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

/**
 * Connector, which can accept drop-in files (i.e. files, uploaded by an user).
 * 
 * @param <T>
 *            type of the connector configuration.
 */
public interface DropInConnector<T extends ConnectorConfig> extends
        FileBasedConnector, Connector<T> {
    /**
     * Loads a list of tasks from the drop-in configuration. Order of loaded
     * tasks is not specified and may depend on implementation.
     * 
     * @param file
     *            file to load data from.
     * @param monitor
     *            can't be null. See
     *            {@link ProgressMonitorUtils#getDummyMonitor()} if you don't
     *            want any monitoring.
     * @throws Exception
     *             some other exceptions the connector might throw
     */
    List<GTask> loadDropInData(File file, Mappings mappings, ProgressMonitor monitor)
            throws ConnectorException;
}
