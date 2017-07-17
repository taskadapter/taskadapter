package com.taskadapter.connector.common

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.definition.exceptions.ConnectorException

/**
  * @tparam N connector-specific ("native") task class
  */
trait BasicIssueSaveAPI[N] {
  /**
    * Creates a new task and returns a new task ID.
    *
    * @param nativeTask native task to create.
    * @return id of the new task. typically this is a database ID (in case of JIRA or Redmine)
    */
  @throws[ConnectorException]
  def createTask(nativeTask: N): TaskId

  /**
    * Updates an existing task.
    *
    * @param nativeTask native task representation.
    */
  @throws[ConnectorException]
  def updateTask(nativeTask: N): Unit
}
