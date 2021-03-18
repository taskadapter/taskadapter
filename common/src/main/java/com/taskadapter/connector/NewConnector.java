package com.taskadapter.connector;

import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;

import java.util.List;

public interface NewConnector {
    /**
     * Connectors should wrap all exceptions inside this method and return all results, including task-specific
     * errors and general errors (like "credentials invalid").
     */
    SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks,
                        List<GTask> tasks,
                        ProgressMonitor monitor,
                        Iterable<FieldRow<?>> rows);

    /**
     * Load list of tasks. Order of loaded tasks is not specified and may depend on implementation.
     * To get tasks in a specific order, use [[com.taskadapter.connector.common.ConnectorUtils]] methods.
     */
    List<GTask> loadData() throws ConnectorException;

    /**
     * Loads one task by its key.
     */
    GTask loadTaskByKey(TaskId key, Iterable<FieldRow<?>> rows) throws ConnectorException;
}
