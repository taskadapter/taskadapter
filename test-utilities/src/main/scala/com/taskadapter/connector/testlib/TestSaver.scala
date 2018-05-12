package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask
import scala.collection.JavaConverters._

class TestSaver(connector: NewConnector, rows: Iterable[FieldRow]) {
  @throws[ConnectorException]
  def saveAndLoad(task: GTask): GTask = {
    val taskSaveResult = connector.saveData(PreviouslyCreatedTasksResolver.empty,
      util.Arrays.asList(task),
      ProgressMonitorUtils.DUMMY_MONITOR,
      rows)
    val newKey = taskSaveResult.getRemoteKeys.iterator.next
    connector.loadTaskByKey(newKey, rows)
  }
}
