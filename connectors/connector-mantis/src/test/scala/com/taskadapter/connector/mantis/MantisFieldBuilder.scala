package com.taskadapter.connector.mantis

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.{Description, Summary}

object MantisFieldBuilder {
  def getDefault(): List[FieldRow[_]] = {
    List(
      FieldRow.apply(Summary, Summary, ""),
      FieldRow.apply(Description, Description, "-")
    )
  }
}

