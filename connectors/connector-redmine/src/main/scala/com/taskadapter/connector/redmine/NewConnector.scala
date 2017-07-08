package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.definition.{ProgressMonitor, TaskSaveResult}
import com.taskadapter.model.GTask

trait NewConnector {
  def saveData(tasks: util.List[GTask], monitor: ProgressMonitor, mappings: util.List[FieldRow]): TaskSaveResult

  def loadData(): util.List[GTask]
}
