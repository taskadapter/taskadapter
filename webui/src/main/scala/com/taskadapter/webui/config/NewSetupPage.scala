package com.taskadapter.webui.config

import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.PluginManager
import com.taskadapter.connector.definition.{ConnectorConfig, ConnectorSetup}
import com.taskadapter.web.event.{EventBusImpl, ShowSetupsListPageRequested}
import com.taskadapter.web.{ConnectorSetupPanel, PluginEditorFactory}
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.pages.SelectConnectorComponent
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._

import scala.collection.JavaConverters._

class NewSetupPage(configOperations: ConfigOperations, editorManager: EditorManager, pluginManager: PluginManager,
                   sandbox: Sandbox) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  val ui = layout
  val plugins = pluginManager.getPluginDescriptors.asScala

  val panelForEditor = new VerticalLayout()
  panelForEditor.setVisible(false)

  val selectConnectorComponent = new SelectConnectorComponent(pluginManager, showAddPanelForConnector).layout

  layout.add(selectConnectorComponent)
  layout.add(panelForEditor)

  private def showAddPanelForConnector(connectorId: String): Unit = {
    // if you remove these class declarations, you will get runtime ClassCastExceptions saying cannot convert
    // WebConnectorSetup to Nothing!
    val editor : PluginEditorFactory[ConnectorConfig, ConnectorSetup] = editorManager.getEditorFactory(connectorId)
    val setup : ConnectorSetup = editor.createDefaultSetup(sandbox)
    val editSetupPanel = editor.getEditSetupPanel(sandbox, setup)
    panelForEditor.removeAll()
    panelForEditor.add(editSetupPanel.getUI)
    val saveButton = new Button(Page.message("newSetupPage.saveButton"))
    saveButton.addClickListener(_ => saveClicked(editSetupPanel))
    val closeButton = new Button(Page.message("newSetupPage.cancelButton"))
    closeButton.addClickListener(_ => EventBusImpl.post(ShowSetupsListPageRequested()))
    panelForEditor.add(new HorizontalLayout(saveButton, closeButton))
    panelForEditor.setVisible(true)
  }

  def saveClicked(panel: ConnectorSetupPanel): Unit = {
    val maybeError = panel.validate
    if (maybeError.isEmpty) {
      configOperations.saveNewSetup(panel.getResult)
      EventBusImpl.post(ShowSetupsListPageRequested())
    } else {
      panel.showError(maybeError.get)
    }
  }
}
