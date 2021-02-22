package com.taskadapter.core;

import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.DropInConnector;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class TaskLoader {
    public static List<GTask> loadTasks(int maxTasksNumber,
                                        NewConnector connectorFrom,
                                        String sourceName,
                                        ProgressMonitor monitor) throws ConnectorException {

        var flatTasksList = connectorFrom.loadData();
        var upToNflatTasksList = getUpToNTasks(maxTasksNumber, flatTasksList);
        return TreeUtils.buildTreeFromFlatList(upToNflatTasksList);
    }

    public static List<GTask> loadDropInTasks(int maxTasksNumber,
                                              DropInConnector connectorFrom,
                                              File dropFile,
                                              ProgressMonitor monitor) throws ConnectorException {
        monitor.beginTask("Loading data from uploaded file", 100);
        var flatTasksList = connectorFrom.loadDropInData(dropFile, monitor);
        flatTasksList.sort(Comparator.comparing(GTask::getId));
        var tasks = TreeUtils.buildTreeFromFlatList(
                getUpToNTasks(maxTasksNumber, flatTasksList));
        monitor.done();
        return tasks;
    }

    private static List<GTask> getUpToNTasks(int maxTasksNumber, List<GTask> flatTasksList) {
        var tasksToLeave = Math.min(maxTasksNumber, flatTasksList.size());
        return flatTasksList.subList(0, tasksToLeave);
    }
}
