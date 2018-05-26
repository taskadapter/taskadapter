package com.taskadapter.connector

import com.taskadapter.connector.definition.{ExportDirection, FieldMapping}
import com.taskadapter.model.Field

object MappingBuilder {
  def build(newMappings: Seq[FieldMapping[_]], exportDirection: ExportDirection): Seq[FieldRow[_]] = {
    newMappings.filter(_.selected).map(mapping => buildRow(mapping, exportDirection))
  }

  private def buildRow[T](mapping: FieldMapping[T], exportDirection: ExportDirection): FieldRow[T] = {
    FieldRow(
      getSourceField(mapping, exportDirection),
      getTargetField(mapping, exportDirection),
      mapping.defaultValue)
  }

  def getSourceField[T](fieldMapping: FieldMapping[T], exportDirection: ExportDirection): Option[Field[T]] = {
    exportDirection match {
      case ExportDirection.RIGHT => fieldMapping.fieldInConnector1
      case ExportDirection.LEFT => fieldMapping.fieldInConnector2
    }
  }

  def getTargetField[T](fieldMapping: FieldMapping[T], exportDirection: ExportDirection): Option[Field[T]] = {
    exportDirection match {
      case ExportDirection.RIGHT => fieldMapping.fieldInConnector2
      case ExportDirection.LEFT => fieldMapping.fieldInConnector1
    }
  }
}
