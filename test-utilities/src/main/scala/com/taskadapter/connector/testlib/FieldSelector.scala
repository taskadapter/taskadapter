package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.connector.definition.AvailableFields
import com.taskadapter.model.GTaskDescriptor

import scala.collection.JavaConverters._

object FieldSelector {
  /**
    * Helper method to get a filtered collection of fields with given `field` and `selected` values.
    * E.g. with `description` field marked as `unselected for export`.
    */
/*
  def getSelectedFields(fields: AvailableFields, field: String, selected: Boolean): util.Collection[GTaskDescriptor.FIELD] = {
    val selectedFields = fields.getSupportedFields.asScala
      .filter((f: GTaskDescriptor.FIELD) => f == field == selected)
    selectedFields.asJavaCollection
  }
*/
}
