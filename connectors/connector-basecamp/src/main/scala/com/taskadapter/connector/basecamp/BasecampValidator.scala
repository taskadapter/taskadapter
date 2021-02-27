package com.taskadapter.connector.basecamp

import com.taskadapter.connector.basecamp.exceptions.BadFieldException
import com.taskadapter.connector.definition.exceptions.BadConfigException

import java.util

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
  private def failIfErrors(errors: java.util.List[BadConfigException]): Unit = {
    if (!errors.isEmpty) throw errors.get(0)
  }

  def validateConfig(config: BasecampConfig): java.util.List[BadConfigException] = {
    val list = new util.ArrayList[BadConfigException]()
    list.addAll(validateAccount(config))
    list.addAll(validateProject(config))
    list.addAll(validateTodolist(config))
    list
  }

  def validateAccount(config: BasecampConfig): java.util.List[BadConfigException] = {
    val list = new util.ArrayList[BadConfigException]()
    val accountId = config.getAccountId
    if (accountId == null || accountId.isEmpty) {
      list.add(FieldNotSetException("account-id"))
    }
    if (!isNum(accountId)) {
      list.add(new BadFieldException("account-id"))
    }
    list
  }

  def validateProject(config: BasecampConfig): java.util.List[BadConfigException] = {
    val list = new util.ArrayList[BadConfigException]()
    val projectKey = config.getProjectKey
    if (projectKey == null) {
      list.add(FieldNotSetException("project-key"))
    }
    if (!isNum(projectKey)) {
      list.add(new BadFieldException("project-key"))
    }
    list
  }

  def validateTodolist(config: BasecampConfig): java.util.List[BadConfigException] = {
    val list = new util.ArrayList[BadConfigException]()
    val listKey = config.getTodoKey
    if (listKey == null) {
      list.add(FieldNotSetException("todo-key"))
    }
    if (!isNum(listKey)) {
      list.add(new BadFieldException("todo-key"))
    }
    list
  }

  private def isNum(str: String): Boolean = str forall Character.isDigit
}
