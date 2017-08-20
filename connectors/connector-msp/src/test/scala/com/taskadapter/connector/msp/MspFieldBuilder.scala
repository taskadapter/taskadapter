package com.taskadapter.connector.msp

import com.taskadapter.connector.FieldRow

object MspFieldBuilder {

  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(MspField.summary, MspField.summary, ""),
    )
  }
}
