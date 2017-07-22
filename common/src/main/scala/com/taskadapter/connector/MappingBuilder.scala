package com.taskadapter.connector

import com.taskadapter.connector.definition.{ExportDirection, FieldMapping}

object MappingBuilder {
  def build(newMappings: Seq[FieldMapping], exportDirection: ExportDirection): Seq[FieldRow] = {
    // TODO TA3 remote id. this was inside cycle.
    //      if (!(row.getField == FIELD.REMOTE_ID.name) || mappingTarget != null)
    newMappings.filter(_.selected).map(row =>
      FieldRow(
        getSourceField(row, exportDirection),
        getTargetField(row, exportDirection),
        row.defaultValue)
    )
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
