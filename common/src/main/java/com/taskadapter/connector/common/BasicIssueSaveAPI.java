package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Basic api for issue saving.
 * 
 * @param <N>
 *            native issue type.
 */
public interface BasicIssueSaveAPI<N> {
    
    /**
     * Creates a new task and returns a new task key.
     * 
     * @param nativeTask
     *            native task to create.
     * @return standard representation of a common task.
     * @throws ConnectorException
     *             if an error occurs.
     */
    public String createTask(N nativeTask) throws ConnectorException;

    /**
     * Updates an existing task.
     * 
     * @param nativeTask
     *            native task representation.
     * @throws ConnectorException
     *             if an error occurs.
     */
    public void updateTask(N nativeTask) throws ConnectorException;

}
