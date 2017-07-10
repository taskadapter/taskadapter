package com.taskadapter.connector.definition

import com.taskadapter.connector.Field

case class FieldMapping(fieldInConnector1: Field, fieldInConnector2: Field, selected: Boolean, defaultValue: String)
