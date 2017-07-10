package com.taskadapter.connector

import java.util

import com.taskadapter.connector.definition.{ExportDirection, FieldMapping}

import scala.collection.JavaConverters._

object MappingBuilder {
  def build(newMappings: util.List[FieldMapping], exportDirection: ExportDirection): java.lang.Iterable[FieldRow] = {
    // TODO TA3 remote id. this was inside cycle.
    //      if (!(row.getField == FIELD.REMOTE_ID.name) || mappingTarget != null)
    newMappings.asScala.map(row =>
      FieldRow(
        getSourceField(row, exportDirection),
        getTargetField(row, exportDirection),
        row.defaultValue)
    ).asJava
  }

  def getSourceField(fieldMapping: FieldMapping, exportDirection: ExportDirection): Field = {
    exportDirection match {
      case ExportDirection.RIGHT => fieldMapping.fieldInConnector1
      case ExportDirection.LEFT => fieldMapping.fieldInConnector2
    }
  }

  def getTargetField(fieldMapping: FieldMapping, exportDirection: ExportDirection): Field = {
    exportDirection match {
      case ExportDirection.RIGHT => fieldMapping.fieldInConnector2
      case ExportDirection.LEFT => fieldMapping.fieldInConnector1
    }
  }
}
