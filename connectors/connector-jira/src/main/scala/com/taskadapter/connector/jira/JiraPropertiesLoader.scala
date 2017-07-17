package com.taskadapter.connector.jira

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.connector.definition.WebServerInfo

object JiraPropertiesLoader {
  private val properties = PropertiesUtf8Loader.load("jira.properties")

  def createTestConfig: JiraConfig = {
    val config = new JiraConfig
    config.setProjectKey(properties.getProperty("project.key"))
    config.setDefaultTaskType(properties.getProperty("defaultTaskType"))
    config.setDefaultIssueTypeForSubtasks(properties.getProperty("defaultSubTaskType"))
    config
  }

  def getTestServerInfo = new WebServerInfo(properties.getProperty("host"),
    properties.getProperty("login"), properties.getProperty("password"))

  def getProjectKey: String = properties.getProperty("project.key")

}
