package com.taskadapter.reporting

import com.google.common.base.Strings
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.Field

object FieldMappingFormatter {
  def format(mappings: Seq[FieldMapping[_]]): String = {
    mappings.map(m => {
      val field1 = formatField(m.fieldInConnector1)
      val field2 = formatField(m.fieldInConnector2)
      s"$field1 - $field2 selected: ${m.selected} default: ${m.defaultValue}"
    })
      .mkString(System.lineSeparator())
  }

  def formatField(field: Option[Field[_]]): String = {
    val string = field.map(f => {
      if (f.getClass.getSimpleName.startsWith("Custom")) {
        f.toString
      } else {
        f.getClass.getSimpleName.replace("$", "")
      }
    }).getOrElse("None")

    Strings.padEnd(string, 30, ' ')
  }
}
