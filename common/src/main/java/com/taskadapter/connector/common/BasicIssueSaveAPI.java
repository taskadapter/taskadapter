package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * @param <N> native issue type.
 */
public interface BasicIssueSaveAPI<N> {
    
    /**
     * Creates a new task and returns a new task ID.
     * 
     * @param nativeTask
     *            native task to create.
     * @return id of the new task. typically this is a database ID (in case of JIRA or Redmine)
     */
    TaskId createTask(N nativeTask) throws ConnectorException;

    /**
     * Updates an existing task.
     * 
     * @param nativeTask native task representation.
     */
    void updateTask(N nativeTask) throws ConnectorException;

}
