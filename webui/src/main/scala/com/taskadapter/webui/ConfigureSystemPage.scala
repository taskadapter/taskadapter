package com.taskadapter.webui

import com.taskadapter.auth.{AuthorizedOperations, CredentialsManager}
import com.taskadapter.license.License
import com.taskadapter.web.SettingsManager
import com.taskadapter.webui.user.UsersPanel
import com.vaadin.data.Property
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
    checkbox.addValueChangeListener(new Property.ValueChangeListener() {
      override def valueChange(valueChangeEvent: Property.ValueChangeEvent): Unit = {
        settingsManager.setAdminCanManageAllConfigs(checkbox.getValue)
      }
    })
    view.addComponent(checkbox)
    view.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT)
    checkbox.setEnabled(modifiable)
    panel
  }

  def render(credentialsManager: CredentialsManager, settings: SettingsManager, license: License,
             authorizedOps: AuthorizedOperations, tracker: Tracker): Component = {
    val layout = new VerticalLayout
    layout.setSpacing(true)
    val cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(settings, authorizedOps.canConfigureServer)
    cmt.setWidth(500, PIXELS)
    layout.addComponent(cmt)
    layout.addComponent(createAdminPermissionsSection(settings, authorizedOps.canConfigureServer))
    layout.addComponent(UsersPanel.render(credentialsManager, authorizedOps, license, tracker))
    layout
  }
}
