package com.taskadapter.connector.mantis

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.{Description, Summary}

object MantisFieldBuilder {
  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Description, "-")
    )
  }
}

