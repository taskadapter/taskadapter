package com.taskadapter.connector.basecamp

import com.taskadapter.connector.definition.exceptions.BadConfigException

case class FieldNotSetException(field: String) extends BadConfigException("Field not set: " + field)