package com.taskadapter.connector

import com.taskadapter.connector.definition.exception.ConfigValidationError
import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.mutable.ListBuffer

class ValidationErrorBuilder {
  val errors = new ListBuffer[ConfigValidationError]()

  def error(exception: BadConfigException): ValidationErrorBuilder = {
    errors += ConfigValidationError(exception, None)
    this
  }

  def build(): Seq[ConfigValidationError] = {
    errors
  }
}
