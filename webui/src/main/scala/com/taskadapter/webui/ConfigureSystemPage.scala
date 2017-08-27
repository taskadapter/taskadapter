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
    val checkbox = new CheckBox(Page.message("configurePage.showAllUsersConfigs"))
    checkbox.setValue(settingsManager.adminCanManageAllConfigs)
    checkbox.setImmediate(true)
    checkbox.addValueChangeListener(_ => settingsManager.setAdminCanManageAllConfigs(checkbox.getValue))
    view.addComponent(checkbox)
    view.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT)
    checkbox.setEnabled(modifiable)
    panel
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
             authorizedOps: AuthorizedOperations, tracker: Tracker): Component = {
    val layout = new VerticalLayout
    layout.setSpacing(true)
    val cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(settingsManager, authorizedOps.canConfigureServer)
    cmt.setWidth(600, PIXELS)
    layout.addComponent(cmt)
    layout.addComponent(createAdminPermissionsSection(settingsManager, authorizedOps.canConfigureServer))
    layout.addComponent(createResultsNumberSection(settingsManager))
    layout.addComponent(new UsersPanel(credentialsManager, authorizedOps, license, tracker).ui)
    layout
  }
}
