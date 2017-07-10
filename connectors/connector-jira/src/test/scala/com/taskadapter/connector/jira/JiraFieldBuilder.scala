package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.FieldRow
import scala.collection.JavaConverters._

object JiraFieldBuilder {
  def getDefault(): util.List[FieldRow] = {
    List(
      FieldRow(JiraField.summary, JiraField.summary, ""),
      FieldRow(JiraField.assignee, JiraField.assignee, ""),
    ).asJava
  }

}
