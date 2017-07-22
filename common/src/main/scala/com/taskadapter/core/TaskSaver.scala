package com.taskadapter.core

import java.util

import com.taskadapter.connector.common.DataConnectorUtil
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.definition.{ProgressMonitor, SaveResult}
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.model.GTask

object TaskSaver {
  def save(taskKeeper: TaskKeeper, connectorTo: NewConnector, destinationName: String, rows: java.lang.Iterable[FieldRow], tasks: util.List[GTask],
           monitor: ProgressMonitor): SaveResult = {
    val totalNumberOfTasks = DataConnectorUtil.calculateNumberOfTasks(tasks)
    monitor.beginTask("Saving " + totalNumberOfTasks + " tasks to " + destinationName, totalNumberOfTasks)
    try {
      val previouslyCreatedTasks = taskKeeper.loadTasks()
      val saveResult = connectorTo.saveData(previouslyCreatedTasks, tasks, monitor, rows)
      monitor.done()
      storeKeys(taskKeeper, saveResult)
      saveResult
    } catch {
      case e: ConnectorException =>
        monitor.done()
        new SaveResult(null, 0, 0, List(), List(e), List())
    }
  }

  def storeKeys(taskKeeper: TaskKeeper, saveResult: SaveResult) : Unit = {
    saveResult.getIdToRemoteKeyList.foreach { case (original, target) =>
      taskKeeper.keepTask(original, target.id)
    }
    taskKeeper.store()
  }
}
