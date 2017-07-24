package com.taskadapter.webui.config

import com.taskadapter.connector.definition.exceptions.BadConfigException

case class FieldNotMappedException(var fieldName: String) extends BadConfigException