package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.DropInConnector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Task loader. Implements strategies to load a list of tasks.
 */
public final class TaskLoader {

    private TaskLoader() {
    }

    public static List<GTask> loadTasks(int maxTasksNumber,
                                        NewConnector connectorFrom, String sourceName,
                                        List<FieldRow> rows, ProgressMonitor monitor)
            throws ConnectorException {
        if (monitor == null) {
            monitor = ProgressMonitorUtils.DUMMY_MONITOR;
        }

        monitor.beginTask("Loading data from " + sourceName, 100);
        List<GTask> flatTasksList = ConnectorUtils.loadDataOrderedById(
                connectorFrom, rows, monitor);
        flatTasksList = getUpToNTasks(maxTasksNumber, flatTasksList);

        final List<GTask> tasks = TreeUtils
                .buildTreeFromFlatList(flatTasksList);
        monitor.done();

        return tasks;
    }

    public static List<GTask> loadDropInTasks(int maxTasksNumber,
            DropInConnector connectorFrom, File dropFile,
            Mappings sourceMappings, ProgressMonitor monitor)
            throws ConnectorException {
        if (monitor == null) {
            monitor = ProgressMonitorUtils.DUMMY_MONITOR;
        }

        monitor.beginTask("Loading data from uploaded file", 100);
        List<GTask> flatTasksList = connectorFrom.loadDropInData(dropFile,
                sourceMappings, monitor);
        Collections.sort(flatTasksList, GTaskUtils.ID_COMPARATOR);
        flatTasksList = getUpToNTasks(maxTasksNumber, flatTasksList);

        final List<GTask> tasks = TreeUtils
                .buildTreeFromFlatList(flatTasksList);
        monitor.done();

        return tasks;
    }

    private static List<GTask> getUpToNTasks(int maxTasksNumber,
            List<GTask> flatTasksList) {
        int tasksToLeave = Math.min(maxTasksNumber, flatTasksList.size());
        return flatTasksList.subList(0, tasksToLeave);
    }
}
