package com.taskadapter.connector.trello

import com.taskadapter.connector.PropertiesUtf8Loader
import com.taskadapter.connector.definition.WebConnectorSetup

object TrelloTestConfig {
  private val TEST_PROPERTIES = "trello.properties"
  private val properties = PropertiesUtf8Loader.load(TEST_PROPERTIES)

  def getConfig = {
    val config = TrelloConfig(properties.getProperty("boardId"))
    config
  }

  def getSetup = WebConnectorSetup(TrelloConnector.ID, "label1",
    "", "", properties.getProperty("apikey"),
    true, properties.getProperty("token"))
}
