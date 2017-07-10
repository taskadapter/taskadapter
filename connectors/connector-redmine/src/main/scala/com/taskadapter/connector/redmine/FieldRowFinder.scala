package com.taskadapter.connector.redmine
import com.taskadapter.connector.FieldRow

import scala.collection.JavaConverters._

object FieldRowFinder {
  def containsTargetField(fieldRow: java.lang.Iterable[FieldRow], fieldName: String) : Boolean = {
    fieldRow.asScala.exists(row => row.targetField.name.equals(fieldName))
  }
}
