package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.EditorUtil.textInput
import com.taskadapter.web.ui.Grids.addTo
import com.taskadapter.webui.Page
import com.vaadin.data.Property
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._

class ServerPanelWithKeyAndToken(connectorId: String, caption: String, val labelProperty: Property[String],
                                 val serverURLProperty: Property[String],
                                 val userLogin: Property[String],
                                 val apiKeyProperty: Property[String],
                                 val tokenProperty: Property[String]) extends ConnectorSetupPanel {
  val panel = new Panel
  panel.setCaption(caption)

  val serverURL = textInput(serverURLProperty)
  val userLoginInput = textInput(userLogin)
  val apiKeyField = new PasswordField
  val tokenField = new PasswordField

  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")

  buildUI(labelProperty, serverURLProperty, apiKeyProperty, tokenProperty)

  private def buildUI(labelProperty: Property[String], serverURLProperty: Property[String],
                      apiKeyProperty: Property[String], tokenProperty: Property[String]) = {
    val grid = new GridLayout

    val layout = new VerticalLayout(grid, errorMessageLabel)
    panel.setContent(layout)
    grid.setSpacing(true)
    grid.setMargin(true)
    grid.setColumns(2)
    grid.setRows(8)

    var currentRow = 0

    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.name")))
    val labelField = textInput(labelProperty)
    labelField.addStyleName("server-panel-textfield")
    grid.addComponent(labelField, 1, currentRow)

    // server url
    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.serverUrl")))
    serverURL.addStyleName("server-panel-textfield")
    serverURL.setInputPrompt("http://myserver:3000/some_location")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, serverURL)

    // user name
    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.login")))
    userLoginInput.addStyleName("server-panel-textfield")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, userLoginInput)

    val emptyLabelHeight = "10px"

    currentRow += 1
    grid.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1

    grid.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.apiAccessKey")))
    apiKeyField.setPropertyDataSource(apiKeyProperty)
    apiKeyField.addStyleName("server-panel-textfield")

    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, apiKeyField)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.token")))
    tokenField.setPropertyDataSource(tokenProperty)
    tokenField.addStyleName("server-panel-textfield")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, tokenField)
  }

  private def createEmptyLabel(height: String) = {
    val label = new Label("&nbsp;", ContentMode.HTML)
    label.setHeight(height)
    label
  }

  override def getUI: Component = panel

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(labelProperty.getValue)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    val host = serverURL.getValue
    if (host == null || host.isEmpty || host.equalsIgnoreCase(ServerPanelWithAPIKey.defaultUrlPrefix)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelProperty.getValue, serverURLProperty.getValue, userLogin.getValue,
      apiKeyField.getValue, true, tokenProperty.getValue)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setValue(string)

  }

}
