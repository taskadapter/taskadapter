package com.taskadapter.web.configeditor.server

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.configeditor.EditorUtil
import com.taskadapter.webui.Page
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.{RadioButtonGroup, RadioGroupVariant}
import com.vaadin.flow.component.{Component, Html}
import com.vaadin.flow.data.binder.Binder

object ServerPanelWithPasswordAndAPIKey {
  val defaultUrlPrefix = "http://"
}

class ServerPanelWithPasswordAndAPIKey(connectorId: String, caption: String,
                                       setup: WebConnectorSetup) extends ConnectorSetupPanel {

  val layout = new VerticalLayout()
  val captionLabel = new Html(s"<b>$caption</b>")
  layout.add(caption)

  val binder = new Binder(classOf[WebConnectorSetup])

  val labelField = EditorUtil.textInput(binder, "label")
  val serverURL = EditorUtil.textInput(binder, "host")
  serverURL.setPlaceholder("http://myserver:3000/some_location")

  val apiKeyField = EditorUtil.passwordInput(binder, "apiKey")
  val login = EditorUtil.textInput(binder, "userName")
  val password = EditorUtil.passwordInput(binder, "password")

  val errorMessageLabel = new Label
  errorMessageLabel.addClassName("error-message-label")

  val authOptionsGroup = new RadioButtonGroup[String]()

  buildUI()
  binder.readBean(setup)

  private def buildUI() = {
    val form = new FormLayout()

    form.add(
      new Label(Page.message("setupPanel.name")),
      labelField)

    form.add(
      new Label(Page.message("setupPanel.serverUrl")),
      serverURL)

    authOptionsGroup.setLabel(Page.message("setupPanel.authorization"))
    authOptionsGroup.setItems(Page.message("setupPanel.useApiKey"),
      Page.message("setupPanel.useLogin"))
    val booleanValue = setup.useApiKey
    val valueToSet = if (booleanValue) {
      Page.message("setupPanel.useApiKey")
    } else {
      Page.message("setupPanel.useLogin")
    }
    authOptionsGroup.setValue(valueToSet)
    authOptionsGroup.addThemeVariants(RadioGroupVariant.MATERIAL_VERTICAL)
    authOptionsGroup.addValueChangeListener(_ => {
      val useAPIOptionSelected = shouldUseApiKey()
      setAuthOptionsState(useAPIOptionSelected)
    })

    form.add(authOptionsGroup, 2)

    form.add(
      new Label(Page.message("setupPanel.apiAccessKey")),
      apiKeyField)

    form.add(
      new Label(Page.message("setupPanel.login")),
      login)

    form.add(
      new Label(Page.message("setupPanel.password")),
      password)

    layout.add(captionLabel, form, errorMessageLabel)
  }

  private def setAuthOptionsState(useAPIKey: java.lang.Boolean) = {
    apiKeyField.setEnabled(useAPIKey)
    login.setEnabled(!useAPIKey)
    password.setEnabled(!useAPIKey)
  }

  override def getUI: Component = layout

  override def validate(): Option[String] = {
    if (Strings.isNullOrEmpty(labelField.getValue)) {
      return Some(Page.message("newConfig.configure.nameRequired"))
    }
    val host = serverURL.getValue
    if (host == null || host.isEmpty || host.equalsIgnoreCase(ServerPanelWithPasswordAndAPIKey.defaultUrlPrefix)) {
      return Some(Page.message("newConfig.configure.serverUrlRequired"))
    }
    None
  }

  override def getResult: WebConnectorSetup = {
    WebConnectorSetup(connectorId, None, labelField.getValue, serverURL.getValue, login.getValue,
      password.getValue, shouldUseApiKey(), apiKeyField.getValue)
  }

  override def showError(string: String): Unit = {
    errorMessageLabel.setText(string)
  }

  private def shouldUseApiKey(): Boolean = {
    authOptionsGroup.getValue.equals(Page.message("setupPanel.useApiKey"))
  }
}
