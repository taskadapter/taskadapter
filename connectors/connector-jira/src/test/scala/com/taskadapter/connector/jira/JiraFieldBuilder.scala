package com.taskadapter.connector.jira

import com.taskadapter.connector.FieldRow

object JiraFieldBuilder {
  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(JiraField.summary, JiraField.summary, ""),
      FieldRow(JiraField.assignee, JiraField.assignee, ""),
    )
  }
}
