package com.taskadapter.connector

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.redmine.{FieldRow, NewConnector, RedmineConfig}
import scala.collection.JavaConverters._

class Adapter(connector1: NewConnector, connector2: NewConnector) {

  private def getAllMappings(): List[FieldRow] = {
    List(
      FieldRow("summary", true, "summary", "summary", ""),
      FieldRow("description", true, "description", "description", "default alex description"),
      FieldRow("done_ratio", true, "done_ratio", "done_ratio", ""),
      FieldRow("due_date", true, "due_date", "due_date", ""),
      FieldRow("assignee", true, "assignee", "assignee", ""),
      FieldRow("", true, "my_custom_1", "my_custom_1", "default custom alex")
    )
  }

  def adapt(): Unit = {
    val tasks = connector1.loadData()
    val fewTasks = tasks.subList(0,2)
    val result = connector2.saveData(fewTasks, ProgressMonitorUtils.DUMMY_MONITOR, getAllMappings().asJava)
    println(result)
  }

}
