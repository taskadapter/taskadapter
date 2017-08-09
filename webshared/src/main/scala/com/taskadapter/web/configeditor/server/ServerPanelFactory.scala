package com.taskadapter.web.configeditor.server

import com.taskadapter.connector.definition.{WebConnectorSetup, WebServerInfo}
import com.taskadapter.web.ConnectorSetupPanel
import com.vaadin.data.util.MethodProperty

object ServerPanelFactory {
  def withApiKeyAndLoginPassword(connectorId: String, caption: String, setupOption: Option[WebConnectorSetup]): ConnectorSetupPanel = {
    val webServerInfo = setupOption.map(s =>
      new WebServerInfo(s.label, s.host, s.userName, s.password, s.useApiKey, s.apiKey)).getOrElse(new WebServerInfo())

      new ServerPanelWithAPIKey(connectorId, caption,
        new MethodProperty[String](webServerInfo, "label"),
        new MethodProperty[String](webServerInfo, "host"),
        new MethodProperty[String](webServerInfo, "userName"),
        new MethodProperty[String](webServerInfo, "password"),
        new MethodProperty[String](webServerInfo, "apiKey"),
        new MethodProperty[java.lang.Boolean](webServerInfo, "useAPIKeyInsteadOfLoginPassword")
      )
  }

  def withLoginAndPassword(connectorId: String, caption: String, setupOption: Option[WebConnectorSetup]): ConnectorSetupPanel = {
    val webServerInfo = setupOption.map(s =>
      new WebServerInfo(s.label, s.host, s.userName, s.password, false, "")).getOrElse(new WebServerInfo())
    new ServerPanel(connectorId, caption,
      new MethodProperty[String](webServerInfo, "label"),
      new MethodProperty[String](webServerInfo, "host"),
      new MethodProperty[String](webServerInfo, "userName"),
      new MethodProperty[String](webServerInfo, "password"))
  }

}
