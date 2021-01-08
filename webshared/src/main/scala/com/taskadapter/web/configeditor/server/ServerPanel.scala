package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.DefaultPanel
import com.taskadapter.webui.Page
import com.vaadin.flow.component.{Component, Html}
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class ServerPanel(connectorId: String, val caption: String, setup: WebConnectorSetup)
  extends ConnectorSetupPanel {

  val captionLabel = new Html(s"<b>$caption</b>")

  var serverContainer = new ServerContainer(setup)
  val errorMessageLabel = new Label
  errorMessageLabel.setVisible(false)

  val layout = new VerticalLayout(captionLabel, serverContainer, errorMessageLabel)
  layout.setWidth(DefaultPanel.NARROW_PANEL_WIDTH)

  override def getUI: Component = layout

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(setup.label)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    if (Strings.isNullOrEmpty(setup.host)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, setup.label, setup.host, setup.userName,
      setup.password, false, "")
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setText(string)
    errorMessageLabel.setVisible(true)
  }
}
