package com.taskadapter.connector.definition;

import java.io.File;
import java.util.List;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.model.GTask;

/**
 * Connector, which can accept drop-in files (i.e. files, uploaded by an user).
 */
public interface DropInConnector extends FileBasedConnector, NewConnector {
    /**
     * Loads a list of tasks from the drop-in configuration. Order of loaded
     * tasks is not specified and may depend on implementation.
     * 
     * @param file
     *            file to load data from.
     * @param monitor
     *            can't be null. See [[ProgressMonitorUtils]] if you don't want any monitoring.
     */
    List<GTask> loadDropInData(File file, Mappings mappings, ProgressMonitor monitor)
            throws ConnectorException;
}
