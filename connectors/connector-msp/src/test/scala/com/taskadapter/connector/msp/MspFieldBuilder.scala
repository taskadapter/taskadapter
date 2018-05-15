package com.taskadapter.connector.msp

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.Summary

object MspFieldBuilder {

  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, "")
    )
  }
}
