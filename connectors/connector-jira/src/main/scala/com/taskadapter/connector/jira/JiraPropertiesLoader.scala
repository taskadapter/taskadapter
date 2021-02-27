package com.taskadapter.connector.jira

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.connector.definition.WebConnectorSetup

object JiraPropertiesLoader {
  private val properties = PropertiesUtf8Loader.load("jira.properties")

  def createTestConfig: JiraConfig = {
    val config = new JiraConfig
    config.setProjectKey(properties.getProperty("project.key"))
    config.setDefaultTaskType(properties.getProperty("defaultTaskType"))
    config.setDefaultIssueTypeForSubtasks(properties.getProperty("defaultSubTaskType"))
    config
  }

  def getTestServerInfo = WebConnectorSetup.apply(JiraConnector.ID, "label1", properties.getProperty("host"),
    properties.getProperty("login"), properties.getProperty("password"), false, "")

  def getProjectKey: String = properties.getProperty("project.key")

}
