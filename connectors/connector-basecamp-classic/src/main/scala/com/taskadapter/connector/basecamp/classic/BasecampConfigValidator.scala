package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.ValidationErrorBuilder
import com.taskadapter.connector.basecamp.FieldNotSetException
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.Seq

object BasecampConfigValidator {
  def validateServerAuth(setup: WebConnectorSetup): Unit = {
    failIfErrors(validateServerAuthNoException(setup))
  }

  def validateServerAuthNoException(setup: WebConnectorSetup): Seq[BadConfigException] = {
    val errorBuilder = new ValidationErrorBuilder
    val apiKey = setup.apiKey
    if (apiKey == null || apiKey.isEmpty) errorBuilder.error(FieldNotSetException("auth"))
    val apiUrl = setup.host
    if (apiUrl == null || apiUrl.isEmpty) errorBuilder.error(FieldNotSetException("api-url"))
    errorBuilder.build()
  }

  def validateProjectKey(config: BasecampClassicConfig): Unit = {
    failIfErrors(validateProjectKeyNoException(config))
  }

  def validateProjectKeyNoException(config: BasecampClassicConfig): Seq[BadConfigException] = {
    val errorBuilder = new ValidationErrorBuilder
    val pKey = config.getProjectKey
    if (pKey == null || pKey.isEmpty) errorBuilder.error(FieldNotSetException("project-key"))
    errorBuilder.build()
  }

  def validateTodoList(config: BasecampClassicConfig): Unit = {
    failIfErrors(validateTodoListNoException(config))
  }

  def validateTodoListNoException(config: BasecampClassicConfig): Seq[BadConfigException] = {
    val errorBuilder = new ValidationErrorBuilder
    val pKey = config.getTodoKey
    if (pKey == null || pKey.isEmpty) errorBuilder.error(FieldNotSetException("todo-key"))
    errorBuilder.build()
  }

  @throws[BadConfigException]
  private def failIfErrors(errors: Seq[BadConfigException]): Unit = {
    if (errors.nonEmpty) throw errors.head
  }
}