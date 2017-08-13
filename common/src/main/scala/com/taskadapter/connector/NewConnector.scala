package com.taskadapter.connector

import java.util

import com.taskadapter.connector.definition.{ProgressMonitor, SaveResult, TaskId}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask

trait NewConnector {
  /**
    * Connectors should wrap all exceptions inside this method and return all results, including task-specific
    * errors and general errors (like "credentials invalid").
    */
  def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver,
               tasks: util.List[GTask],
               monitor: ProgressMonitor,
               rows: Iterable[FieldRow]): SaveResult

  /**
    * Load list of tasks. Order of loaded tasks is not specified and may depend on implementation.
    * To get tasks in a specific order, use [[com.taskadapter.connector.common.ConnectorUtils]] methods.
    */
  def loadData(): util.List[GTask]

  /**
    * Loads one task by its key.
    */
  def loadTaskByKey(key: TaskId, rows: java.lang.Iterable[FieldRow]): GTask
}
