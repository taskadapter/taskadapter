package com.taskadapter.connector.redmine

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.{AssigneeFullName, Summary}

object RedmineFieldBuilder {

  def withAssignee(loginName: String = null): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, ""),
      FieldRow(AssigneeFullName, AssigneeFullName, loginName)
    )
  }

  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, "")
    )
  }
}
