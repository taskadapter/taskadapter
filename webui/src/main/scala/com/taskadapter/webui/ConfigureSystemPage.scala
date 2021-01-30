package com.taskadapter.webui

import com.taskadapter.auth.{AuthorizedOperations, CredentialsManager}
import com.taskadapter.license.License
import com.taskadapter.web.SettingsManager
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.pages.Navigator
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.user.UsersPanel
import com.vaadin.flow.component.{Component, HasComponents}
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route

@Route(value = Navigator.CONFIGURE_SYSTEM, layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class ConfigureSystemPage() extends BasePage {

  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices

  render(services.credentialsManager,
    services.settingsManager,
    services.licenseManager.getLicense,
    configOps.authorizedOps)

  private def createAdminPermissionsSection(settingsManager: SettingsManager, modifiable: Boolean) = {
    val view = new VerticalLayout
    addCheckbox(view, Page.message("configurePage.showAllUsersConfigs"), settingsManager.schedulerEnabled, modifiable,
      newValue => settingsManager.setSchedulerEnabled(newValue))

    addCheckbox(view, Page.message("configurePage.anonymousErrorReporting"), settingsManager.isErrorReportingEnabled, modifiable,
      newValue => settingsManager.setErrorReporting(newValue))

    view
  }

  private def addCheckbox(view: VerticalLayout, label: String, value: Boolean, modifiable: Boolean, listener: Boolean => Unit) = {
    val checkbox = new Checkbox(label)
    checkbox.setValue(value)
//    checkbox.setImmediate(true)
    checkbox.addValueChangeListener(_ => listener.apply(checkbox.getValue))
    view.add(checkbox)
//    view.setComponentAlignment(checkbox, Alignment.MIDDLE_LEFT)
    checkbox.setEnabled(modifiable)
  }

  def createResultsNumberSection(settingsManager: SettingsManager): Component = {
    val view = new VerticalLayout
    val field = new TextField(Page.message("configurePage.maxNumberOfResultsToSave"))
//    field.setDescription(Page.message("configurePage.maxNumberExplanation"))
    field.setValue(settingsManager.getMaxNumberOfResultsToKeep + "")
    field.addValueChangeListener(_ => settingsManager.setMaxNumberOfResultsToKeep(field.getValue.toInt))
    view.add(field)
    view
  }

  def render(credentialsManager: CredentialsManager, settingsManager: SettingsManager, license: License,
             authorizedOps: AuthorizedOperations): Unit = {

    setSpacing(true)
    val cmt = LocalRemoteOptionsPanel.createLocalRemoteOptions(settingsManager, authorizedOps.canConfigureServer)
    add(cmt)
    val allowedToEdit = authorizedOps.canConfigureServer && license != null
    add(createAdminPermissionsSection(settingsManager, allowedToEdit))
    add(createResultsNumberSection(settingsManager))
    add(new UsersPanel(credentialsManager, authorizedOps, license))

  }
}
