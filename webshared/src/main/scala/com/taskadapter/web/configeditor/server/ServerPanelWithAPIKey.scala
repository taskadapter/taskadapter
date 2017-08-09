package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.{WebConnectorSetup, WebServerInfo}
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.EditorUtil._
import com.taskadapter.web.ui.Grids._
import com.taskadapter.webui.Page
import com.vaadin.data.Property
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._

import scala.collection.JavaConverters._

class ServerPanelWithAPIKey(connectorId: String, caption: String, val labelProperty: Property[String],
                            val serverURLProperty: Property[String], val loginNameProperty: Property[String],
                            val passwordProperty: Property[String], val apiKeyProperty: Property[String],
                            val useApiKeyProperty: Property[java.lang.Boolean]) extends ConnectorSetupPanel {
  val panel = new Panel
  panel.setCaption(caption)

  val serverURL = textInput(serverURLProperty)
  val apiKeyField = new PasswordField
  val login = textInput(loginNameProperty)
  val password = new PasswordField

  val authOptions = List[java.lang.Boolean](true, false).asJava
  val authOptionsGroup = new OptionGroup(Page.message("setupPanel.authorization"), authOptions)
  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")

  buildUI(labelProperty, serverURLProperty, loginNameProperty, passwordProperty, apiKeyProperty, useApiKeyProperty)
  addListener()
  setAuthOptionsState(authOptionsGroup.getValue.asInstanceOf[Boolean])

  private def buildUI(labelProperty: Property[String], serverURLProperty: Property[String],
                      loginNameProperty: Property[String], passwordProperty: Property[String],
                      apiKeyProperty: Property[String], useApiKeyProperty: Property[java.lang.Boolean]) = {
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

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.serverUrl")))
    serverURL.addStyleName("server-panel-textfield")
    serverURL.setInputPrompt("http://myserver:3000/some_location")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, serverURL)
    val emptyLabelHeight = "10px"

    currentRow += 1
    grid.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1

    authOptionsGroup.setItemCaption(true, Page.message("setupPanel.useApiKey"))
    authOptionsGroup.setItemCaption(false, Page.message("setupPanel.useLogin"))
    authOptionsGroup.setPropertyDataSource(useApiKeyProperty)
    authOptionsGroup.setSizeFull()
    authOptionsGroup.setNullSelectionAllowed(false)
    authOptionsGroup.setImmediate(true)

    grid.addComponent(authOptionsGroup, 0, currentRow, 1, currentRow)
    grid.setComponentAlignment(authOptionsGroup, Alignment.MIDDLE_LEFT)

    currentRow += 1
    grid.addComponent(createEmptyLabel(emptyLabelHeight), 0, currentRow)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.apiAccessKey")))
    apiKeyField.setPropertyDataSource(apiKeyProperty)
    apiKeyField.addStyleName("server-panel-textfield")

    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, apiKeyField)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.login")))
    login.addStyleName("server-panel-textfield")
    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, login)

    currentRow += 1
    addTo(grid, 0, currentRow, Alignment.MIDDLE_LEFT, new Label(Page.message("setupPanel.password")))
    password.addStyleName("server-panel-textfield")
    password.setPropertyDataSource(passwordProperty)

    addTo(grid, 1, currentRow, Alignment.MIDDLE_LEFT, password)
  }

  private def createEmptyLabel(height: String) = {
    val label = new Label("&nbsp;", ContentMode.HTML)
    label.setHeight(height)
    label
  }

  private def addListener() = {
    authOptionsGroup.addValueChangeListener(_ => {
      val useAPIOptionSelected = authOptionsGroup.getValue.asInstanceOf[java.lang.Boolean]
      setAuthOptionsState(useAPIOptionSelected)
    })
  }

  private def setAuthOptionsState(useAPIKey: java.lang.Boolean) = {
    apiKeyField.setEnabled(useAPIKey)
    login.setEnabled(!useAPIKey)
    password.setEnabled(!useAPIKey)
  }

  override def getUI: Component = panel

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(labelProperty.getValue)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    val host = serverURL.getValue
    if (host == null || host.isEmpty || host.equalsIgnoreCase(WebServerInfo.DEFAULT_URL_PREFIX)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelProperty.getValue, serverURLProperty.getValue, loginNameProperty.getValue,
      passwordProperty.getValue, useApiKeyProperty.getValue, apiKeyProperty.getValue)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setValue(string)
  }
}
