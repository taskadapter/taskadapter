package com.taskadapter.connector.redmine
import java.util

import scala.collection.JavaConverters._

object FieldRowFinder {
  def containsGenericField(fieldRow: util.Collection[FieldRow], fieldName: String) : Boolean = {
    fieldRow.asScala.exists(row => row.genericFieldName.equals(fieldName))
  }
}
