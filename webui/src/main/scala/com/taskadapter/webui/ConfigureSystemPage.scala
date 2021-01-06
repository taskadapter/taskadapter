package com.taskadapter.webui

import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.auth.{AuthorizedOperations, CredentialsManager}
import com.taskadapter.license.License
import com.taskadapter.web.SettingsManager
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.user.UsersPanel
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._

class ConfigureSystemPage() {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices

  def ui: VerticalLayout = ConfigureSystemPanel.render(services.credentialsManager, services.settingsManager, services.licenseManager.getLicense,
  configOps.authorizedOps)
}

object ConfigureSystemPanel {
  private def createAdminPermissionsSection(settingsManager: SettingsManager, modifiable: Boolean) = {
    val panel = new Panel
    val view = new VerticalLayout
    view.setMargin(true)
    panel.setContent(view)
    addCheckbox(view, Page.message("configurePage.showAllUsersConfigs"), settingsManager.schedulerEnabled, modifiable,
      newValue => settingsManager.setSchedulerEnabled(newValue))

    addCheckbox(view, Page.message("configurePage.anonymousErrorReporting"), settingsManager.isErrorReportingEnabled, modifiable,
      newValue => settingsManager.setErrorReporting(newValue))

    panel
  }

  private def addCheckbox(view: VerticalLayout, label: String, value: Boolean, modifiable: Boolean, listener: Boolean => Unit) = {
    val checkbox = new CheckBox(label)
    checkbox.setValue(value)
    checkbox.setImmediate(true)
    checkbox.addValueChangeListener(_ => listener.apply(checkbox.getValue))
    view.add(checkbox)
    view.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT)
    checkbox.setEnabled(modifiable)
  }

  def createResultsNumberSection(settingsManager: SettingsManager): Component = {
    val panel = new Panel
    val view = new VerticalLayout
    view.setMargin(true)
    panel.setContent(view)
    val field = new TextField(Page.message("configurePage.maxNumberOfResultsToSave"))
    field.setDescription(Page.message("configurePage.maxNumberExplanation"))
    field.setValue(settingsManager.getMaxNumberOfResultsToKeep + "")
    field.setImmediate(true)
    field.addValueChangeListener(_ => settingsManager.setMaxNumberOfResultsToKeep(field.getValue.toInt))
    view.add(field)

    panel
  }

  def render(credentialsManager: CredentialsManager, settingsManager: SettingsManager, license: License,
             authorizedOps: AuthorizedOperations): VerticalLayout = {
    EventTracker.trackPage("system_configuration")

    val layout = new VerticalLayout
    layout.setSpacing(true)
    val cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(settingsManager, authorizedOps.canConfigureServer)
    cmt.setWidth(600, PIXELS)
    layout.add(cmt)
    val allowedToEdit = authorizedOps.canConfigureServer && license != null
    layout.add(createAdminPermissionsSection(settingsManager, allowedToEdit))
    layout.add(createResultsNumberSection(settingsManager))
    layout.add(new UsersPanel(credentialsManager, authorizedOps, license).ui)
    layout
  }
}
