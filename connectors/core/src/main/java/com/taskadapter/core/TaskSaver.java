package com.taskadapter.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.taskadapter.connector.common.DataConnectorUtil;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.common.TreeUtils;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskError;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskErrorsBuilder;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public final class TaskSaver {
    private TaskSaver() {
    }

    public static SyncResult<TaskSaveResult, TaskErrors<ConnectorError<Throwable>>> save(
            Connector<?> connectorFrom, String sourceConnectorId, Mappings sourceMappings,
            Connector<?> connectorTo, String destinationConnectorId, Mappings destinationMappings,
            List<GTask> tasks, ProgressMonitor monitor) throws ConnectorException {
        if (monitor == null) {
            monitor = ProgressMonitorUtils.getDummyMonitor();
        }
        
        final int totalNumberOfTasks = DataConnectorUtil.calculateNumberOfTasks(tasks);
        monitor.beginTask("Saving " + totalNumberOfTasks + " tasks to "
                + connectorTo.getConfig().getTargetLocation(), totalNumberOfTasks);
        
        final List<GTask> treeToSave;
        if (destinationMappings.isFieldSelected(FIELD.REMOTE_ID)) {
            List<GTask> clonedTree = TreeUtils.cloneTree(tasks);
            TaskUtil.setRemoteIdField(clonedTree);
            treeToSave = clonedTree;
        } else {
            treeToSave = tasks;
        }

        TaskSaveResult saveResult;
        final TaskErrorsBuilder<ConnectorError<Throwable>> errors = new TaskErrorsBuilder<ConnectorError<Throwable>>();

        try {
            final SyncResult<TaskSaveResult, TaskErrors<Throwable>> saveTaskResult = connectorTo
                    .saveData(treeToSave, monitor, destinationMappings);
            saveResult = saveTaskResult.getResult();
            errors.addErrors(connectorizeTasks(saveTaskResult.getErrors()
                    .getErrors(), destinationConnectorId));
            errors.addGeneralErrors(connectorize(saveTaskResult.getErrors()
                    .getGeneralErrors(), destinationConnectorId));
        } catch (ConnectorException e) {
            saveResult = null;
            errors.addGeneralError(new ConnectorError<Throwable>(e, sourceConnectorId));
        }

        if (monitor != null) {
            monitor.done();
        }

        // TODO !!! fix remote IDs
        System.err.println("FIX REMOTE IDs!");
/*
        if (saveResult != null
                && configFrom.getFieldMappings().isFieldSelected(
                FIELD.REMOTE_ID) && (saveResult.getUpdatedTasksNumber() + saveResult.getCreatedTasksNumber()) > 0) {
            try {
                connectorFrom.updateRemoteIDs(configFrom,
                        saveResult.getIdToRemoteKeyMap(), null, destinationMappings);
            } catch (ConnectorException e) {
                errors.addGeneralError(new ConnectorError<Throwable>(e,
                        destinationConnectorId));
            }
        }
*/

        return new SyncResult<TaskSaveResult, TaskErrors<ConnectorError<Throwable>>>(
                saveResult, errors.getResult());

    }
    
    private static <T> List<TaskError<ConnectorError<T>>> connectorizeTasks(Collection<TaskError<T>> errors, String connectorId) {
        final List<TaskError<ConnectorError<T>>> result = new ArrayList<TaskError<ConnectorError<T>>>(errors.size());
        for (TaskError<T> error : errors)
            result.add(new TaskError<ConnectorError<T>>(error.getTask(), new ConnectorError<T>(error.getErrors(), connectorId)));
        return result;
    }

    private static <T> List<ConnectorError<T>> connectorize(Collection<T> errors, String connectorId) {
        final List<ConnectorError<T>> result = new ArrayList<ConnectorError<T>>(errors.size());
        for (T error : errors)
            result.add(new ConnectorError<T>(error, connectorId));
        return result;
    }

}
