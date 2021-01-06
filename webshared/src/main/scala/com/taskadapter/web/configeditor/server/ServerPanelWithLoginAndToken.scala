package com.taskadapter.web.configeditor.server

import com.taskadapter.vaadin14shim.Label
import com.taskadapter.vaadin14shim.PasswordField
import com.taskadapter.vaadin14shim.Label
import com.google.common.base.Strings
import com.taskadapter.vaadin14shim.GridLayout
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.EditorUtil.textInput
import com.taskadapter.web.ui.Grids.addTo
import com.taskadapter.webui.Page
import com.vaadin.data.Property
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._

class ServerPanelWithLoginAndToken(connectorId: String, caption: String, val labelProperty: Property[String],
                                   val serverURLProperty: Property[String],
                                   val userLogin: Property[String],
                                   val apiTokenProperty: Property[String],
                                   tokenDescription: String) extends ConnectorSetupPanel {
  val panel = new Panel
  panel.setCaption(caption)

  private val serverURL = textInput(serverURLProperty)
  private val userLoginInput = textInput(userLogin)
  private val apiTokenField = new PasswordField

  val errorMessageLabel = new Label
  errorMessageLabel.addClassName("error-message-label")

  buildUI(labelProperty)

  private def buildUI(labelProperty: Property[String]) = {
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
    labelField.addClassName("server-panel-textfield")
    grid.add(labelField, 1, currentRow)

    // server url
    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.serverUrl")))
    serverURL.addClassName("server-panel-textfield")
    serverURL.setInputPrompt("https://myserver:3000/some_location")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, serverURL)

    // user name
    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.login")))
    userLoginInput.addClassName("server-panel-textfield")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, userLoginInput)

    val emptyLabelHeight = "10px"

    currentRow += 1
    grid.add(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1
    addTo(grid, Alignment.MIDDLE_LEFT, new Label(tokenDescription, ContentMode.HTML))

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT,
      new Label(Page.message("setupPanel.token"), ContentMode.HTML))
    apiTokenField.setPropertyDataSource(apiTokenProperty)
    apiTokenField.addClassName("server-panel-textfield")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, apiTokenField)
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
    if (host == null || host.isEmpty || host.equalsIgnoreCase(ServerPanelWithPasswordAndAPIKey.defaultUrlPrefix)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelProperty.getValue, serverURLProperty.getValue, userLogin.getValue,
      "", true, apiTokenProperty.getValue)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setValue(string)

  }

}
