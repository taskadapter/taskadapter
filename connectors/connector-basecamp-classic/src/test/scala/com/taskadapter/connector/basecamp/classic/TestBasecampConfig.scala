package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.definition.WebConnectorSetup

object TestBasecampConfig {
  private val PROJECT_KEY = "12955684-testproject"

  def config: BasecampClassicConfig = {
    val config = new BasecampClassicConfig
    config.setProjectKey(PROJECT_KEY)
    config.setLookupUsersByName(true)
    config
  }

  def setup = WebConnectorSetup.apply(BasecampClassicConnector.ID, "label",
    "https://altadev.basecamphq.com", "", "", true, "ba1bf0af26c0f1e55f92aac5c2447a1576a398cd")
}