package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

/**
 * Basic api for issue saving.
 * 
 * @param <N>
 *            native issue type.
 */
public interface BasicIssueSaveAPI<N> {
    /**
     * Creates a new task and returns an internal task representation.
     * 
     * @param nativeTask
     *            native task to create.
     * @return standard representation of a common task.
     * @throws ConnectorException
     *             if an error occurs.
     */
    public GTask createTask(N nativeTask) throws ConnectorException;

    /**
     * Updates an existing task.
     * 
     * @param taskId
     *            task id to update.
     * @param nativeTask
     *            native task representation.
     * @throws ConnectorException
     *             if an error occurs.
     */
    public void updateTask(String taskId, N nativeTask)
            throws ConnectorException;

}
