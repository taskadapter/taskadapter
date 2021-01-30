package com.taskadapter.webui.config

import com.taskadapter.connector.definition.{ConnectorConfig, ConnectorSetup}
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.{ConnectorSetupPanel, PluginEditorFactory}
import com.taskadapter.webui.pages.{Navigator, SelectConnectorComponent}
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{BasePage, ConfigOperations, EventTracker, Layout, Page, SessionController}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.router.Route

import scala.collection.JavaConverters._

@Route(value = Navigator.NEW_SETUP, layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class NewSetupPage extends BasePage {

  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()
  private val pluginManager = services.pluginManager
  private val editorManager = services.editorManager

  val plugins = pluginManager.getPluginDescriptors.asScala

  val panelForEditor = new VerticalLayout()
  panelForEditor.setVisible(false)

  val selectConnectorComponent = new SelectConnectorComponent(pluginManager, showAddPanelForConnector).layout

  add(selectConnectorComponent, panelForEditor)

  private def showAddPanelForConnector(connectorId: String): Unit = {
    // if you remove these class declarations, you will get runtime ClassCastExceptions saying cannot convert
    // WebConnectorSetup to Nothing!
    val editor: PluginEditorFactory[ConnectorConfig, ConnectorSetup] = editorManager.getEditorFactory(connectorId)
    val setup: ConnectorSetup = editor.createDefaultSetup(sandbox)
    val editSetupPanel = editor.getEditSetupPanel(sandbox, setup)
    panelForEditor.removeAll()
    panelForEditor.add(editSetupPanel.getUI)
    val saveButton = new Button(Page.message("newSetupPage.saveButton"))
    saveButton.addClickListener(_ => saveClicked(editSetupPanel))
    val closeButton = new Button(Page.message("newSetupPage.cancelButton"))
    closeButton.addClickListener(_ => Navigator.setupsList())
    panelForEditor.add(new HorizontalLayout(saveButton, closeButton))
    panelForEditor.setVisible(true)
  }

  def saveClicked(panel: ConnectorSetupPanel): Unit = {
    val maybeError = panel.validate
    if (maybeError.isEmpty) {
      configOps.saveNewSetup(panel.getResult)
      Navigator.setupsList()
    } else {
      panel.showError(maybeError.get)
    }
  }
}
