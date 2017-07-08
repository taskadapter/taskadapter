package com.taskadapter.connector

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskSaveResult
import com.taskadapter.core.NewConnector

import scala.collection.JavaConverters._

class Adapter(connector1: NewConnector, connector2: NewConnector) {

  def adapt(rows: List[FieldRow]): TaskSaveResult = {
    val tasks = connector1.loadData()
    val result = connector2.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    result
  }

}
