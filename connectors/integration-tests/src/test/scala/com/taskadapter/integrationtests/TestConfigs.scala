package com.taskadapter.integrationtests

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.jira.{JiraConfig, JiraConnector}
import com.taskadapter.connector.redmine.{RedmineConfig, RedmineConnector}

object TestConfigs {
  private val properties = PropertiesUtf8Loader.load("redmine_test_data.properties")
  private val jiraProperties = PropertiesUtf8Loader.load("jira.properties")

  def getRedmineConfig:RedmineConfig = {
    val redmineConfig = new RedmineConfig
    redmineConfig.setProjectKey(properties.getProperty("project.key"))
    redmineConfig
  }

  def getRedmineSetup = WebConnectorSetup(RedmineConnector.ID, "label1", properties.getProperty("uri"), "", "",
    true, properties.getProperty("apikey"))

  def getJiraConfig:JiraConfig = {
    val config = new JiraConfig
    config.setProjectKey(jiraProperties.getProperty("project.key"))
    config.setQueryId(jiraProperties.getProperty("queryId").toInt)
    config
  }

  def getJiraSetup = WebConnectorSetup(JiraConnector.ID, "label1", jiraProperties.getProperty("host"),
    jiraProperties.getProperty("login"), jiraProperties.getProperty("password"), false, "")
}
