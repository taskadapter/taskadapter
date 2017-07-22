package com.taskadapter.connector

import java.util

import com.taskadapter.connector.definition.{ProgressMonitor, SaveResult}
import com.taskadapter.model.GTask

trait NewConnector {
  def saveData(previouslyCreatedTasks: Map[String, scala.Long],
               tasks: util.List[GTask],
               monitor: ProgressMonitor,
               rows: java.lang.Iterable[FieldRow]): SaveResult

  /**
    * Load list of tasks. Order of loaded tasks is not specified and may depend on implementation.
    * To get tasks in a specific order, use [[com.taskadapter.connector.common.ConnectorUtils]] methods.
    */
  def loadData(): util.List[GTask]

  /**
    * Loads one task by its key.
    */
  def loadTaskByKey(key: String, rows: java.lang.Iterable[FieldRow]): GTask
}
