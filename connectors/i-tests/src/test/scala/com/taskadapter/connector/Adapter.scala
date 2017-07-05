package com.taskadapter.connector

import com.taskadapter.connector.redmine.{FieldRow, NewConnector, RedmineConfig}

class Adapter(connector1: NewConnector, connector2: NewConnector) {

  private def getAllMappings: List[FieldRow] = {
    List(
      FieldRow("summary", true, "content", "default summary"),
      FieldRow("done_ratio", true, "done_ratio", "default done ratio"),
      FieldRow("due_date", true, "due_date", "default due date"),
      FieldRow("assignee", true, "assignee", "default assignee")
    )
  }

  def adapt(sourceConfig: RedmineConfig): Unit = {
    val tasks = connector1.loadData()

  }

}
