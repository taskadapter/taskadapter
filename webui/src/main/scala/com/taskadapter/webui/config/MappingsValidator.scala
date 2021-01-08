package com.taskadapter.webui.config

import com.google.common.base.Strings
import com.taskadapter.connector.definition.exception.FieldNotMappedException
import com.taskadapter.connector.definition.exceptions.BadConfigException

object MappingsValidator {
  @throws[BadConfigException]
  def validate(mappings: Iterable[EditableFieldMapping]) = {
    validateAllSelectedFieldsMappedToSomething(mappings)
  }

  @throws[FieldNotMappedException]
  private def validateAllSelectedFieldsMappedToSomething(mappings: Iterable[EditableFieldMapping]) = {
    mappings.foreach { row =>

      val valid = (row.getFieldInConnector1 != "" && row.getFieldInConnector2 != "") ||
        (row.getFieldInConnector1 != "" && row.getFieldInConnector2 == ""  && row.getDefaultValue != "") ||
        (row.getFieldInConnector2 != "" && row.getFieldInConnector1 == ""  && row.getDefaultValue != "")

      if (row.getSelected && !valid) {
        var string = Strings.nullToEmpty(row.getFieldInConnector1)
        if (string != "" && row.getFieldInConnector2 != "") {
          string = " "
        }

        string += Strings.nullToEmpty(row.getFieldInConnector2)
        throw FieldNotMappedException(string)
      }
    }
  }
}
