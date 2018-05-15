package com.taskadapter.connector.basecamp

import com.taskadapter.connector.FieldRow

object BasecampFieldBuilder {
  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(BasecampField.content, BasecampField.content, ""),
    )
  }
}
