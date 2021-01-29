package com.taskadapter.core

import java.util

import com.taskadapter.connector.common.DataConnectorUtil
import com.taskadapter.connector.definition.{ProgressMonitor, SaveResult}
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.model.GTask
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
object TaskSaver {
  private val log = LoggerFactory.getLogger(TaskSaver.getClass)

  def save(previouslyCreatedTasks: PreviouslyCreatedTasksResolver,
           connectorTo: NewConnector,
           destinationName: String,
           rows: Seq[FieldRow[_]],
           tasks: util.List[GTask],
           monitor: ProgressMonitor): SaveResult = {
    val totalNumberOfTasks = DataConnectorUtil.calculateNumberOfTasks(tasks)
    val str = "Saving " + totalNumberOfTasks + " tasks to " + destinationName
    log.info(str)
    monitor.beginTask(str, totalNumberOfTasks)
    try {
      val saveResult = connectorTo.saveData(previouslyCreatedTasks, tasks, monitor, rows.asJava)
      monitor.done()
      saveResult
    } catch {
      case e: Exception =>
        monitor.done()
        log.error(s"Exception in connector $connectorTo while saving data. destination: $destinationName. Exception is: $e")
        SaveResult(null, 0, 0, List(), List(e), List())
    }
  }
}
