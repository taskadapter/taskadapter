package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.basecamp.FieldNotSetException
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.{Seq, mutable}

object BasecampConfigValidator {
  def validateServerAuth(setup: WebConnectorSetup): Unit = {
    failIfErrors(validateServerAuthNoException(setup))
  }

  def validateServerAuthNoException(setup: WebConnectorSetup): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    val apiKey = setup.getApiKey
    if (apiKey == null || apiKey.isEmpty) seq += FieldNotSetException("auth")
    val apiUrl = setup.getHost
    if (apiUrl == null || apiUrl.isEmpty) seq += FieldNotSetException("api-url")
    seq
  }

  def validateProjectKey(config: BasecampClassicConfig): Unit = {
    failIfErrors(validateProjectKeyNoException(config))
  }

  def validateProjectKeyNoException(config: BasecampClassicConfig): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    val pKey = config.getProjectKey
    if (pKey == null || pKey.isEmpty) seq += FieldNotSetException("project-key")
    seq
  }

  def validateTodoList(config: BasecampClassicConfig): Unit = {
    failIfErrors(validateTodoListNoException(config))
  }

  def validateTodoListNoException(config: BasecampClassicConfig): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    val pKey = config.getTodoKey
    if (pKey == null || pKey.isEmpty) seq += FieldNotSetException("todo-key")
    seq
  }

  @throws[BadConfigException]
  private def failIfErrors(errors: Seq[BadConfigException]): Unit = {
    if (errors.nonEmpty) throw errors.head
  }
}