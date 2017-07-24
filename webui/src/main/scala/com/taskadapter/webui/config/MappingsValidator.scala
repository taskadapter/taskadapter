package com.taskadapter.webui.config

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
      notMapped = mapping.fieldInConnector1 == null || mapping.fieldInConnector2 == null
      val string = mapping.fieldInConnector1 + " " + mapping.fieldInConnector2
      if (mapping.selected && notMapped) throw new FieldNotMappedException(string)
    }
  }
}
