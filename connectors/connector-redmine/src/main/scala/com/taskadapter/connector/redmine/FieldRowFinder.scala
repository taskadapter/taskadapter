package com.taskadapter.connector.redmine
import java.util

import com.taskadapter.connector.FieldRow

import scala.collection.JavaConverters._

object FieldRowFinder {
  def containsTargetField(fieldRow: util.Collection[FieldRow], fieldName: String) : Boolean = {
    fieldRow.asScala.exists(row => row.nameInTarget.equals(fieldName))
  }
}
