package com.taskadapter.web.configeditor.server

import com.taskadapter.connector.definition.WebServerInfo
import com.taskadapter.web.ConnectorSetupPanel
import com.vaadin.data.util.MethodProperty

object ServerPanelFactory {
  def withApiKeyAndLoginPassword(connectorId: String, caption: String, webServerInfo: WebServerInfo): ConnectorSetupPanel = {
      new ServerPanelWithAPIKey(connectorId, caption,
        new MethodProperty[String](webServerInfo, "label"),
        new MethodProperty[String](webServerInfo, "host"),
        new MethodProperty[String](webServerInfo, "userName"),
        new MethodProperty[String](webServerInfo, "password"),
        new MethodProperty[String](webServerInfo, "apiKey"),
        new MethodProperty[java.lang.Boolean](webServerInfo, "useAPIKeyInsteadOfLoginPassword")
      )
  }

  def withLoginAndPassword(connectorId: String, caption: String, webServerInfo: WebServerInfo): ConnectorSetupPanel = {
    new ServerPanel(connectorId, caption,
      new MethodProperty[String](webServerInfo, "label"),
      new MethodProperty[String](webServerInfo, "host"),
      new MethodProperty[String](webServerInfo, "userName"),
      new MethodProperty[String](webServerInfo, "password"))
  }

}
