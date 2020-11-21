package com.taskadapter.webui

import com.taskadapter.auth.{AuthorizedOperations, CredentialsManager}
import com.taskadapter.license.License
import com.taskadapter.web.SettingsManager
import com.taskadapter.webui.user.UsersPanel
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._


object ConfigureSystemPage {
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
    view.addComponent(checkbox)
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
    view.addComponent(field)

    panel
  }

  def render(credentialsManager: CredentialsManager, settingsManager: SettingsManager, license: License,
             authorizedOps: AuthorizedOperations): ComponentContainer = {
    val layout = new VerticalLayout
    layout.setSpacing(true)
    val cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(settingsManager, authorizedOps.canConfigureServer)
    cmt.setWidth(600, PIXELS)
    layout.addComponent(cmt)
    val allowedToEdit = authorizedOps.canConfigureServer && license != null
    layout.addComponent(createAdminPermissionsSection(settingsManager, allowedToEdit))
    layout.addComponent(createResultsNumberSection(settingsManager))
    layout.addComponent(new UsersPanel(credentialsManager, authorizedOps, license).ui)
    layout
  }
}
