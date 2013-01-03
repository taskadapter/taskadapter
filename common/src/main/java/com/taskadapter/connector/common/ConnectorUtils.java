package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskUtils;

import java.util.Collections;
import java.util.List;

/**
 * methods for general connector interface.
 */
public final class ConnectorUtils {

    /**
     * Loads tasks sorted by ID.
     *
     * @param connector connector to fetch data from.
     * @param monitor   monitor to use.
     * @return loaded tasks list, sorted by task ID.
     * @throws ConnectorException
     */
    @Deprecated
    public static List<GTask> loadDataOrderedById(Connector<?> connector, Mappings sourceMappings, ProgressMonitor monitor) throws ConnectorException {
        final List<GTask> tasks = connector.loadData(sourceMappings, monitor);
        Collections.sort(tasks, GTaskUtils.ID_COMPARATOR);
        return tasks;
    }

    /**
     * Loads tasks sorted by ID.
     *
     * @param connector connector to fetch data from.
     * @return loaded tasks list, sorted by task ID.
     * @throws ConnectorException
     */
    @Deprecated
    public static List<GTask> loadDataOrderedById(Connector<?> connector, Mappings sourceMappings) throws ConnectorException {
        return loadDataOrderedById(connector, sourceMappings, ProgressMonitorUtils.getDummyMonitor());
    }
}
