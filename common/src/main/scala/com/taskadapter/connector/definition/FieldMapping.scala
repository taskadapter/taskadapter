package com.taskadapter.connector.definition

import com.taskadapter.model.Field

object FieldMapping {
  def apply[T](fieldInConnector1: Field[T], fieldInConnector2: Field[T], selected: Boolean, defaultValue: T): FieldMapping[T] = {
    FieldMapping(Some(fieldInConnector1), Some(fieldInConnector2), selected, defaultValue)
  }
}

case class FieldMapping[T](fieldInConnector1: Option[Field[T]], fieldInConnector2: Option[Field[T]], selected: Boolean, defaultValue: T)
