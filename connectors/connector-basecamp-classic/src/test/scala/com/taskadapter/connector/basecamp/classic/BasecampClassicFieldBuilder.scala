package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.FieldRow

object BasecampClassicFieldBuilder {
  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(BasecampClassicField.content, BasecampClassicField.content, ""),
    )
  }
}
