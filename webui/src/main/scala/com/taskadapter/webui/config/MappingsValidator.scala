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
    for (mapping <- mappings) {
      var notMapped = false
      notMapped = mapping.fieldInConnector1 == "" || mapping.fieldInConnector2 == ""
      var string = Strings.nullToEmpty(mapping.fieldInConnector1)
      if (string != "" && mapping.fieldInConnector2 != "") {
        string = " "
      }

      string += Strings.nullToEmpty(mapping.fieldInConnector2)
      if (mapping.selected && notMapped) throw FieldNotMappedException(string)
    }
  }
}
