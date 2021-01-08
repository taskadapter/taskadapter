package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.webui.Page
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.{Component, Html}
import com.vaadin.flow.data.binder.Binder

class ServerPanelWithKeyAndToken(connectorId: String, caption: String, setup: WebConnectorSetup) extends ConnectorSetupPanel {
  val captionLabel = new Html(s"<b>$caption</b>")

  val layout = new VerticalLayout()

  private val binder = new Binder(classOf[WebConnectorSetup])

  val labelField = ServerPanelUtil.label(binder)
  labelField.addClassName("server-panel-textfield")

  val hostField = ServerPanelUtil.host(binder)
  val userLoginInput = ServerPanelUtil.userName(binder)

  val apiKeyField = ServerPanelUtil.apiKey(binder)
  apiKeyField.addClassName("server-panel-textfield")

  val tokenField = ServerPanelUtil.password(binder)
  tokenField.addClassName("server-panel-textfield")

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

    form.add(
      new Label(Page.message("setupPanel.apiAccessKey")),
      apiKeyField)

    form.add(
      new Label(Page.message("setupPanel.token")),
      tokenField)

    layout.add(captionLabel, form, errorMessageLabel)
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
      apiKeyField.getValue, true, tokenField.getValue)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setText(string)
  }
}
