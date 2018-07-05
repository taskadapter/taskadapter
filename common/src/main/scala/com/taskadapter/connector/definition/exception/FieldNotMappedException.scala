package com.taskadapter.connector.definition.exception

import com.taskadapter.connector.definition.exceptions.BadConfigException

case class FieldNotMappedException(var fieldName: String) extends BadConfigException