package com.taskadapter.integrationtests

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.SaveResult
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver

class Adapter(connector1: NewConnector, connector2: NewConnector) {

  def adapt(rows: List[FieldRow]): SaveResult = {
    val tasks = connector1.loadData()
    val result = connector2.saveData(PreviouslyCreatedTasksResolver.empty, tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    result
  }

}
