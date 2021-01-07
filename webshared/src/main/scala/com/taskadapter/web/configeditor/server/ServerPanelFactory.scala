package com.taskadapter.web.configeditor.server

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel

object ServerPanelFactory {
  def withApiKeyAndLoginPassword(connectorId: String, caption: String, setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanelWithPasswordAndAPIKey(connectorId, caption, setup)
  }

  def withLoginAndPassword(connectorId: String, caption: String, setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanel(connectorId, caption, setup)
  }

  def withLoginAndApiToken(connectorId: String, caption: String, tokenDescription: String,
                           setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanelWithLoginAndToken(connectorId, caption, setup, tokenDescription)
  }

  def withApiKeyAndToken(connectorId: String, caption: String, setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanelWithKeyAndToken(connectorId, caption, setup)
  }
}
