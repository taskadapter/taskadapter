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
      /*
                  if (mapping.getField().equals(GTaskDescriptor.FIELD.REMOTE_ID.name())) {
                      // TODO !!! this is a hack.  fix Remote ID mapping.
                      notMapped = mapping.getConnector1() == null && mapping.getConnector2() == null;
                  } else {
      */ notMapped = mapping.fieldInConnector1 == null || mapping.fieldInConnector2 == null
      //            }
      if (mapping.selected && notMapped) throw new FieldNotMappedException(mapping)
    }
  }
}
