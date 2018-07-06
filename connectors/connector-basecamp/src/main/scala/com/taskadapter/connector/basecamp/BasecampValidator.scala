package com.taskadapter.connector.basecamp

import com.taskadapter.connector.basecamp.exceptions.BadFieldException
import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.{Seq, mutable}

object BasecampValidator {

  @throws[BadConfigException]
  def validateConfigWithException(config: BasecampConfig): Unit = {
    failIfErrors(validateConfig(config))
  }

  @throws[BadConfigException]
  def validateAccountWithException(config: BasecampConfig): Unit = {
    failIfErrors(validateAccount(config))
  }

  @throws[BadConfigException]
  def validateProjectWithException(config: BasecampConfig): Unit = {
    failIfErrors(validateProject(config))
  }

  @throws[BadConfigException]
  private def failIfErrors(errors: Seq[BadConfigException]): Unit = {
    if (errors.nonEmpty) throw errors.head
  }

  def validateConfig(config: BasecampConfig): Seq[BadConfigException] = {
    validateAccount(config) ++ validateProject(config) ++ validateTodolist(config)
  }

  def validateAccount(config: BasecampConfig): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    val accountId = config.getAccountId
    if (accountId == null || accountId.isEmpty) seq += FieldNotSetException("account-id")
    if (!isNum(accountId)) seq += new BadFieldException("account-id")
    seq
  }

  def validateProject(config: BasecampConfig): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    val projectKey = config.getProjectKey
    if (projectKey == null) seq += FieldNotSetException("project-key")
    if (!isNum(projectKey)) seq += new BadFieldException("project-key")
    seq
  }

  def validateTodolist(config: BasecampConfig): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    val listKey = config.getTodoKey
    if (listKey == null) seq += FieldNotSetException("todo-key")
    if (!isNum(listKey)) seq += new BadFieldException("todo-key")
    seq
  }

  private def isNum(str: String): Boolean = str forall Character.isDigit
}
