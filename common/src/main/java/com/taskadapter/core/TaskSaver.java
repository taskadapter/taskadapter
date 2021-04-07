package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.DataConnectorUtil;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.model.GTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskSaver {
    private static final Logger log = LoggerFactory.getLogger(TaskSaver.class);

    public static SaveResult save(PreviouslyCreatedTasksResolver previouslyCreatedTasks,
                           NewConnector connectorTo,
                           String destinationName,
                           Iterable<FieldRow<?>> rows,
                           List<GTask> tasks,
                           ProgressMonitor monitor) {
        var totalNumberOfTasks = DataConnectorUtil.calculateNumberOfTasks(tasks);
        var str = "Saving " + totalNumberOfTasks + " tasks to " + destinationName;
        log.info(str);
        monitor.beginTask(str, totalNumberOfTasks);
        try {
            var saveResult = connectorTo.saveData(previouslyCreatedTasks, tasks, monitor, rows);
            monitor.done();
            return saveResult;
        } catch (Exception e) {
            monitor.done();
            log.error("Exception in connector " + connectorTo + " while saving data. destination: " + destinationName + ". The exception is: " + e);
            return new SaveResult(null, 0, 0,
                    List.of(),
                    List.of(e),
                    List.of());
        }
    }

}

