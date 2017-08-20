package com.taskadapter.connector.redmine

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.connector.definition.WebConnectorSetup

object RedmineTestConfig {
  private val TEST_PROPERTIES = "redmine.properties"
  private val properties = PropertiesUtf8Loader.load(TEST_PROPERTIES)

  def getRedmineTestConfig = {
    val redmineConfig = new RedmineConfig
    redmineConfig.setProjectKey(properties.getProperty("project.key"))
    redmineConfig
  }

  def getRedmineServerInfo = WebConnectorSetup(RedmineConnector.ID, "label1", properties.getProperty("uri"), "", "",
    true, properties.getProperty("apikey"))
}
