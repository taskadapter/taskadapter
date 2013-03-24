package com.taskadapter.core;

import com.taskadapter.connector.common.DataConnectorUtil;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.util.Collections;
import java.util.List;

public final class TaskSaver {
    private TaskSaver() {
    }

    public static TaskSaveResult save(Connector<?> connectorTo,
            String destinationName, Mappings destinationMappings,
            List<GTask> tasks, ProgressMonitor monitor) {
        if (monitor == null) {
            monitor = ProgressMonitorUtils.getDummyMonitor();
        }
        
        final int totalNumberOfTasks = DataConnectorUtil.calculateNumberOfTasks(tasks);
        monitor.beginTask("Saving " + totalNumberOfTasks + " tasks to "
                + destinationName, totalNumberOfTasks);
        
        TaskSaveResult saveResult;

        try {
            saveResult = connectorTo.saveData(tasks, monitor,
                    destinationMappings);
        } catch (ConnectorException e) {
            saveResult = new TaskSaveResult("", 0, 0, Collections.<Integer, String>emptyMap(),
                    Collections.<Throwable>singletonList(e), Collections.<TaskError<Throwable>> emptyList());
        }

        monitor.done();

        return saveResult;
    }
}
