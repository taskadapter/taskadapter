package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exception.SetupNameMissingException
import com.taskadapter.connector.definition.exceptions.{BadConfigException, ServerURLNotSetException}
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.{DefaultPanel, Validatable}
import com.vaadin.data.Property
import com.vaadin.ui.{Component, Panel}


class ServerPanel(connectorId: String, val caption: String, val labelProperty: Property[String], val serverURLProperty: Property[String],
                  val userLoginNameProperty: Property[String], val passwordProperty: Property[String])
  extends ConnectorSetupPanel with Validatable {

  var serverContainer = new ServerContainer(labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty)
  var panel = new Panel
  panel.setWidth(DefaultPanel.NARROW_PANEL_WIDTH)
  panel.setContent(serverContainer)
  panel.setCaption(caption)

  override def getUI: Component = panel

  @throws[BadConfigException]
  override def validate(): Unit = {
    if (Strings.isNullOrEmpty(labelProperty.getValue)) throw new SetupNameMissingException
    if (Strings.isNullOrEmpty(serverURLProperty.getValue)) throw new ServerURLNotSetException
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelProperty.getValue, serverURLProperty.getValue, userLoginNameProperty.getValue,
      passwordProperty.getValue, false, "")
  }
}
