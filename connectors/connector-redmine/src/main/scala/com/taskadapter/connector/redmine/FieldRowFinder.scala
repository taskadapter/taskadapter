package com.taskadapter.connector.redmine

import com.taskadapter.connector.FieldRow

object FieldRowFinder {
  def containsTargetField(fieldRows: Seq[FieldRow], fieldName: String): Boolean = {
    fieldRows.flatMap(_.targetField).exists(f => f.name.equals(fieldName))
  }
}
