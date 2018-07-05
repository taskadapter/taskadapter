package com.taskadapter.connector.definition.exception

import com.taskadapter.connector.definition.exceptions.BadConfigException

case class ConfigValidationError(error: BadConfigException, fixHandler: Option[Runnable])
