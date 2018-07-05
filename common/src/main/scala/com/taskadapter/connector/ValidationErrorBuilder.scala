package com.taskadapter.connector

import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.mutable.ListBuffer

class ValidationErrorBuilder {
  val errors = new ListBuffer[BadConfigException]()

  def error(exception: BadConfigException): ValidationErrorBuilder = {
    errors += exception
    this
  }

  def build(): Seq[BadConfigException] = {
    errors
  }
}
