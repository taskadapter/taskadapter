package com.taskadapter.connector.definition

import com.taskadapter.connector.Field

object FieldMapping {
  def apply(fieldInConnector1: Field, fieldInConnector2: Field, selected: Boolean, defaultValue: String): FieldMapping = {
    FieldMapping(Some(fieldInConnector1), Some(fieldInConnector2), selected, defaultValue)
  }
}

case class FieldMapping(fieldInConnector1: Option[Field], fieldInConnector2: Option[Field], selected: Boolean, defaultValue: String)
