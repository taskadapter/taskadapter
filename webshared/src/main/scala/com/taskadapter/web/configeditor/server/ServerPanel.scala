package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.DefaultPanel
import com.taskadapter.webui.Page
import com.vaadin.data.Property
import com.vaadin.ui.themes.ValoTheme
import com.vaadin.ui.{Component, Label, Panel, VerticalLayout}

class ServerPanel(connectorId: String, val caption: String, val labelProperty: Property[String], val serverURLProperty: Property[String],
                  val userLoginNameProperty: Property[String], val passwordProperty: Property[String])
  extends ConnectorSetupPanel {

  var serverContainer = new ServerContainer(labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty)
  var panel = new Panel
  panel.setWidth(DefaultPanel.NARROW_PANEL_WIDTH)
  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName(ValoTheme.LABEL_FAILURE)
  errorMessageLabel.setVisible(false)

  val layout = new VerticalLayout(serverContainer, errorMessageLabel)
  panel.setContent(layout)
  panel.setCaption(caption)

  override def getUI: Component = panel

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(labelProperty.getValue)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    if (Strings.isNullOrEmpty(serverURLProperty.getValue)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelProperty.getValue, serverURLProperty.getValue, userLoginNameProperty.getValue,
      passwordProperty.getValue, false, "")
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setValue(string)
    errorMessageLabel.setVisible(true)
  }
}
