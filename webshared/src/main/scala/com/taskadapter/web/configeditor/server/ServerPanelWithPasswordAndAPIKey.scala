package com.taskadapter.web.configeditor.server

import com.taskadapter.vaadin14shim.{GridLayout, Label, VerticalLayout}
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.ui.Grids._
import com.taskadapter.webui.Page
import com.vaadin.shared.ui.label.ContentMode
import com.google.common.base.Strings
import com.vaadin.ui.{Alignment, Component, OptionGroup, Panel}

import scala.collection.JavaConverters._

object ServerPanelWithPasswordAndAPIKey {
  val defaultUrlPrefix = "http://"
}

class ServerPanelWithPasswordAndAPIKey(connectorId: String, caption: String, setup: WebConnectorSetup)
  extends ConnectorSetupPanel {

  val panel = new Panel
  panel.setCaption(caption)

  val serverURL = ServerPanelUtil.host(setup)
  val apiKeyField = ServerPanelUtil.apiKey(setup)
  val login = ServerPanelUtil.userName(setup)
  val password = ServerPanelUtil.password(setup)

  val authOptions = List[java.lang.Boolean](true, false).asJava
  val authOptionsGroup = new OptionGroup(Page.message("setupPanel.authorization"), authOptions)
  val errorMessageLabel = new Label
  errorMessageLabel.addClassName("error-message-label")

  buildUI(setup)
  addListener()
  setAuthOptionsState(authOptionsGroup.getValue.asInstanceOf[Boolean])

  private def buildUI(setup: WebConnectorSetup) = {
    val grid = new GridLayout

    val layout = new VerticalLayout(grid, errorMessageLabel)
    panel.setContent(layout)
    grid.setSpacing(true)
    grid.setMargin(true)
    grid.setColumns(2)
    grid.setRows(8)

    var currentRow = 0

    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.name")))
    val labelField = ServerPanelUtil.label(setup)
    labelField.addClassName("server-panel-textfield")
    grid.add(labelField, 1, currentRow)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.serverUrl")))
    serverURL.addClassName("server-panel-textfield")
    serverURL.setInputPrompt("http://myserver:3000/some_location")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, serverURL)
    val emptyLabelHeight = "10px"

    currentRow += 1
    grid.add(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1

    authOptionsGroup.setItemCaption(true, Page.message("setupPanel.useApiKey"))
    authOptionsGroup.setItemCaption(false, Page.message("setupPanel.useLogin"))
    authOptionsGroup.setSizeFull()
    authOptionsGroup.setNullSelectionAllowed(false)
    authOptionsGroup.setImmediate(true)

    grid.add(authOptionsGroup, 0, currentRow, 1, currentRow)
    grid.setComponentAlignment(authOptionsGroup, Alignment.MIDDLE_LEFT)

    currentRow += 1
    grid.add(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.apiAccessKey")))
    apiKeyField.addClassName("server-panel-textfield")

    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, apiKeyField)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.login")))
    login.addClassName("server-panel-textfield")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, login)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.password")))
    password.addClassName("server-panel-textfield")

    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, password)
  }

  private def createEmptyLabel(height: String) = {
    val label = new Label("&nbsp;", ContentMode.HTML)
    label.setHeight(height)
    label
  }

  private def addListener() = {
    authOptionsGroup.setValue(setup.useApiKey)
    authOptionsGroup.addValueChangeListener(_ => {
      val useAPIOptionSelected = authOptionsGroup.getValue.asInstanceOf[java.lang.Boolean]
      setAuthOptionsState(useAPIOptionSelected)
      setup.useApiKey = useAPIOptionSelected
    })
  }

  private def setAuthOptionsState(useAPIKey: java.lang.Boolean) = {
    apiKeyField.setEnabled(useAPIKey)
    login.setEnabled(!useAPIKey)
    password.setEnabled(!useAPIKey)
  }

  override def getUI: Component = panel

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(setup.label)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    val host = serverURL.getValue
    if (host == null || host.isEmpty || host.equalsIgnoreCase(ServerPanelWithPasswordAndAPIKey.defaultUrlPrefix)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, setup.label, setup.host, setup.userName,
      setup.password, setup.useApiKey, setup.apiKey)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setValue(string)
  }
}
