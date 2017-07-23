package com.taskadapter.webui.config

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException

object MappingsValidator {
  @throws[BadConfigException]
  def validate(mappings: Seq[FieldMapping]) = {
    validateAllSelectedFieldsMappedToSomething(mappings)
  }

  @throws[FieldNotMappedException]
  private def validateAllSelectedFieldsMappedToSomething(mappings: Seq[FieldMapping]) = {
    for (mapping <- mappings) {
      var notMapped = false
      notMapped = mapping.fieldInConnector1 == null || mapping.fieldInConnector2 == null
      if (mapping.selected && notMapped) throw new FieldNotMappedException(mapping)
    }
  }
}
