package com.taskadapter.connector.jira

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.{AssigneeLoginName, Summary, TaskStatus}

object JiraFieldBuilder {
  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, ""),
      FieldRow(AssigneeLoginName, AssigneeLoginName, null)
    )
  }

  def withStatus(): List[FieldRow[_]] = {
    List(FieldRow(TaskStatus, TaskStatus, "")) ++ getDefault()
  }
}
