package com.taskadapter.connector.redmine

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.{Assignee, GUser, Summary}

object RedmineFieldBuilder {

  def withAssignee(defaultValue: GUser = null): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Assignee, Assignee, defaultValue)
    )
  }

  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, "")
    )
  }
}
