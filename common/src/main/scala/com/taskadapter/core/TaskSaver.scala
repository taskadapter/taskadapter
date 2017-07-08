package com.taskadapter.core

import java.util
import java.util.Collections

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.DataConnectorUtil
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.definition.{ProgressMonitor, TaskError, TaskSaveResult}
import com.taskadapter.model.GTask

object TaskSaver {
  def save(connectorTo: NewConnector, destinationName: String, rows: util.List[FieldRow], tasks: util.List[GTask],
           monitor: ProgressMonitor): TaskSaveResult = {
    val totalNumberOfTasks = DataConnectorUtil.calculateNumberOfTasks(tasks)
    monitor.beginTask("Saving " + totalNumberOfTasks + " tasks to " + destinationName, totalNumberOfTasks)
    try {
      val saveResult = connectorTo.saveData(tasks, monitor, rows)
      monitor.done()
      saveResult
    } catch {
      case e: ConnectorException =>
        monitor.done()
        new TaskSaveResult(null, 0, 0, Collections.emptyMap[Integer, String], Collections.singletonList[Throwable](e), Collections.emptyList[TaskError[Throwable]])
    }
  }
}
