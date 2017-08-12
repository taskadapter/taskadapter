package com.taskadapter.connector.mantis

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.connector.definition.WebConnectorSetup

object MantisTestConfig {
  private val TEST_PROPERTIES = "mantis.properties"
  private val properties = PropertiesUtf8Loader.load(TEST_PROPERTIES)

  def getSetup = WebConnectorSetup(MantisConnector.ID, "label1", properties.getProperty("uri"),
    properties.getProperty("user"),
    properties.getProperty("password"),
    false, "")
}
