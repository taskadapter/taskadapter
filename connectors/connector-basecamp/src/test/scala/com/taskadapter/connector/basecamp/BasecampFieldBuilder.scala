package com.taskadapter.connector.basecamp

import com.taskadapter.connector.FieldRow

object BasecampFieldBuilder {
  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(BasecampField.content, BasecampField.content, ""),
    )
  }
}
