package com.taskadapter.web.configeditor.server

import com.taskadapter.connector.definition.WebServerInfo
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui.Panel

object ServerPanelFactory {
  def withApiKeyAndLoginPassword(caption: String, webServerInfo: WebServerInfo): Panel = {
      new ServerPanelWithAPIKey(caption,
        new MethodProperty[String](webServerInfo, "label"),
        new MethodProperty[String](webServerInfo, "host"),
        new MethodProperty[String](webServerInfo, "userName"),
        new MethodProperty[String](webServerInfo, "password"),
        new MethodProperty[String](webServerInfo, "apiKey"),
        new MethodProperty[java.lang.Boolean](webServerInfo, "useAPIKeyInsteadOfLoginPassword")
      )
  }

  /*
    def withApi() : ServerPanelWithAPIKey = {

    }
  */

  def withLoginAndPassword(caption: String, webServerInfo: WebServerInfo): Panel = {
    new ServerPanel(caption,
      new MethodProperty[String](webServerInfo, "label"),
      new MethodProperty[String](webServerInfo, "host"),
      new MethodProperty[String](webServerInfo, "userName"),
      new MethodProperty[String](webServerInfo, "password"))
  }

}
