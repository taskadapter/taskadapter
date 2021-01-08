package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.ui.HtmlLabel
import com.taskadapter.webui.Page
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.{Component, Html}
import com.vaadin.flow.data.binder.Binder

class ServerPanelWithLoginAndToken(connectorId: String, caption: String,
                                   setup: WebConnectorSetup,
                                   tokenDescription: String) extends ConnectorSetupPanel {
  val layout = new VerticalLayout()
  val captionLabel = new Html(s"<b>$caption</b>")

  val binder = new Binder(classOf[WebConnectorSetup])

  val labelField = ServerPanelUtil.label(binder)
  labelField.addClassName("server-panel-textfield")

  private val hostField = ServerPanelUtil.host(binder)
  hostField.addClassName("server-panel-textfield")
  hostField.setPlaceholder("https://myserver:3000/some_location")

  private val userLoginInput = ServerPanelUtil.userName(binder)
  userLoginInput.addClassName("server-panel-textfield")

  private val apiTokenField = ServerPanelUtil.apiKey(binder)
  apiTokenField.addClassName("server-panel-textfield")

  binder.readBean(setup)

  val errorMessageLabel = new Label
  errorMessageLabel.addClassName("error-message-label")

  buildUI()

  private def buildUI() = {
    val form = new FormLayout()

    form.add(
      new Label(Page.message("setupPanel.name")),
      labelField)

    form.add(
      new Label(Page.message("setupPanel.serverUrl")),
      hostField)

    form.add(
      new Label(Page.message("setupPanel.login")),
      userLoginInput)

    val emptyLabelHeight = "10px"
    val emptyLabel = createEmptyLabel(emptyLabelHeight)
    form.add(
      emptyLabel,
      emptyLabel)

    form.add(new HtmlLabel(tokenDescription), 2)

    form.add(
      new HtmlLabel(Page.message("setupPanel.token")),
      apiTokenField)

    layout.add(captionLabel, form, errorMessageLabel)
  }

  private def createEmptyLabel(height: String) = {
    val label = new HtmlLabel("&nbsp;")
    label.setHeight(height)
    label
  }

  override def getUI: Component = layout

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(labelField.getValue)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    val host = hostField.getValue
    if (host == null || host.isEmpty || host.equalsIgnoreCase(ServerPanelWithPasswordAndAPIKey.defaultUrlPrefix)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelField.getValue, hostField.getValue, userLoginInput.getValue,
      "", true, apiTokenField.getValue)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setText(string)
  }
}
