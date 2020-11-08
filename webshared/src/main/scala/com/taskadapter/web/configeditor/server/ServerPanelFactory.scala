package com.taskadapter.web.configeditor.server

import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.vaadin.data.util.ObjectProperty

object ServerPanelFactory {
  def withApiKeyAndLoginPassword(connectorId: String, caption: String, setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanelWithPasswordAndAPIKey(connectorId, caption,
      new ObjectProperty[String](setup.label),
      new ObjectProperty[String](setup.host),
      new ObjectProperty[String](setup.userName),
      new ObjectProperty[String](setup.password),
      new ObjectProperty[String](setup.apiKey),
      new ObjectProperty[java.lang.Boolean](setup.useApiKey)
    )
  }

  def withLoginAndPassword(connectorId: String, caption: String, setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanel(connectorId, caption,
      new ObjectProperty[String](setup.label),
      new ObjectProperty[String](setup.host),
      new ObjectProperty[String](setup.userName),
      new ObjectProperty[String](setup.password)
    )
  }

  def withEmailAndApiToken(connectorId: String, caption: String, tokenDescription: String,
                           setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanelWithLoginAndToken(connectorId, caption,
      new ObjectProperty[String](setup.label),
      new ObjectProperty[String](setup.host),
      new ObjectProperty[String](setup.userName),
      new ObjectProperty[String](setup.apiKey),
      tokenDescription
    )
  }

  def withApiKeyAndToken(connectorId: String, caption: String, setup: WebConnectorSetup): ConnectorSetupPanel = {
    new ServerPanelWithKeyAndToken(connectorId, caption,
      new ObjectProperty[String](setup.label),
      new ObjectProperty[String](setup.host),
      new ObjectProperty[String](setup.userName),
      new ObjectProperty[String](setup.password),
      new ObjectProperty[String](setup.apiKey)
    )
  }
}
