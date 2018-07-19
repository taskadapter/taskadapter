package com.taskadapter.connector.redmine

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.Summary

object RedmineFieldBuilder {

  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, "")
    )
  }
}
