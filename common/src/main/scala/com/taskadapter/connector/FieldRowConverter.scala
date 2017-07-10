package com.taskadapter.connector

import com.taskadapter.connector.definition.AvailableFields

import scala.collection.JavaConverters._

object FieldRowConverter {
  private val defaultValueForEmpty = ""

 /* def rows(availableFields: AvailableFields): java.lang.Iterable[FieldRow] = {
    availableFields.getFields.asScala
      .map(legacy =>
        FieldRow(
          Field(legacy._2.getClassForField(), legacy._1),
          legacy._1, defaultValueForEmpty)
      ).asJava
  }*/
}
