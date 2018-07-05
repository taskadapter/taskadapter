package com.taskadapter.connector.basecamp

import com.taskadapter.connector.ValidationErrorBuilder
import com.taskadapter.connector.basecamp.exceptions.BadFieldException
import com.taskadapter.connector.definition.exception.ConfigValidationError
import com.taskadapter.connector.definition.exceptions.BadConfigException

import scala.collection.Seq

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
  private def failIfErrors(errors: Seq[ConfigValidationError]): Unit = {
    if (errors.nonEmpty) throw errors.head.error
  }

  def validateConfig(config: BasecampConfig): Seq[ConfigValidationError] = {
    validateAccount(config) ++ validateProject(config) ++ validateTodolist(config)
  }

  def validateAccount(config: BasecampConfig): Seq[ConfigValidationError] = {
    val errorBuilder = new ValidationErrorBuilder
    val accountId = config.getAccountId
    if (accountId == null || accountId.isEmpty) errorBuilder.error(FieldNotSetException("account-id"))
    if (!isNum(accountId)) errorBuilder.error(new BadFieldException("account-id"))
    errorBuilder.build()
  }

  def validateProject(config: BasecampConfig): Seq[ConfigValidationError] = {
    val errorBuilder = new ValidationErrorBuilder
    val projectKey = config.getProjectKey
    if (projectKey == null) errorBuilder.error(FieldNotSetException("project-key"))
    if (!isNum(projectKey)) errorBuilder.error(new BadFieldException("project-key"))
    errorBuilder.build()
  }

  def validateTodolist(config: BasecampConfig): Seq[ConfigValidationError] = {
    val errorBuilder = new ValidationErrorBuilder
    val listKey = config.getTodoKey
    if (listKey == null) errorBuilder.error(FieldNotSetException("todo-key"))
    if (!isNum(listKey)) errorBuilder.error(new BadFieldException("todo-key"))
    errorBuilder.build()
  }

  private def isNum(str: String): Boolean = str forall Character.isDigit
}
