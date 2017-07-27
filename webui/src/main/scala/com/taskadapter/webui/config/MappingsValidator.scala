package com.taskadapter.webui.config

import com.google.common.base.Strings
import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.JavaConverters._

object MappingsValidator {
  @throws[BadConfigException]
  def validate(mappings: java.util.List[EditableFieldMapping]) = {
    validateAllSelectedFieldsMappedToSomething(mappings.asScala)
  }

  @throws[FieldNotMappedException]
  private def validateAllSelectedFieldsMappedToSomething(mappings: Seq[EditableFieldMapping]) = {
    mappings.foreach { row =>

      val valid = (row.fieldInConnector1 != "" && row.fieldInConnector2 != "") ||
        (row.fieldInConnector1 != "" && row.fieldInConnector2 == ""  && row.defaultValue != "") ||
        (row.fieldInConnector2 != "" && row.fieldInConnector1 == ""  && row.defaultValue != "")

      if (row.selected && !valid) {
        var string = Strings.nullToEmpty(row.fieldInConnector1)
        if (string != "" && row.fieldInConnector2 != "") {
          string = " "
        }

        string += Strings.nullToEmpty(row.fieldInConnector2)
        throw FieldNotMappedException(string)
      }
    }
  }
}
