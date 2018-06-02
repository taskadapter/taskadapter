package com.taskadapter.connector.common

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.Field

object FieldMappingBuilder {
  def getMapping[T](field: Field[T]): FieldMapping[T] = {
    FieldMapping.apply(field, field, true, null.asInstanceOf[String])
  }
}
