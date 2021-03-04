package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.FieldRow

object BasecampClassicFieldBuilder {
  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow.apply(BasecampClassicField.content, BasecampClassicField.content, ""),
    )
  }
}
