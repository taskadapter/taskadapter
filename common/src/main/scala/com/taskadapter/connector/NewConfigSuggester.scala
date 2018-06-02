package com.taskadapter.connector

import com.taskadapter.connector.common.FieldMappingBuilder
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.Field

/**
  * Given two maps of available connector-specific fields (for connector 1 and connector 2),
  * build a list of [[FieldMapping]] with suggested mappings: what fields in connector 1
  * are equivalent to fields in connector 2.
  * Equivalent means they refer to the same [[Field]].
  */
object NewConfigSuggester {

  /**
    * try to match list of fields for connector 1 with the list for connector 2.
    */
  def suggestedFieldMappingsForNewConfig(list1: Seq[Field[_]], list2: Seq[Field[_]]): Seq[FieldMapping[_]] = {
    list1.intersect(list2).map(f => FieldMappingBuilder.getMapping(f))
  }
}
