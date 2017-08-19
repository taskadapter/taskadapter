package com.taskadapter.webui.config

import com.taskadapter.PluginManager
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.pages.SelectConnectorComponent
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._

import scala.collection.JavaConverters._

class NewSetupPage(configOperations: ConfigOperations, editorManager: EditorManager, pluginManager: PluginManager,
                   sandbox: Sandbox, onDone: () => Unit) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  val ui = layout
  val plugins = pluginManager.getPluginDescriptors.asScala

  val panelForEditor = new VerticalLayout()
  panelForEditor.setVisible(false)

  val selectConnectorComponent = new SelectConnectorComponent(pluginManager, showAddPanelForConnector).layout

  layout.addComponent(selectConnectorComponent)
  layout.addComponent(panelForEditor)

  private def showAddPanelForConnector(connectorId: String): Unit = {
    val editor = editorManager.getEditorFactory(connectorId)
    val editSetupPanel = editor.getEditSetupPanel(sandbox, None)
    panelForEditor.removeAllComponents()
    panelForEditor.addComponent(editSetupPanel.getUI)
    val saveButton = new Button(Page.message("newSetupPage.saveButton"))
    saveButton.addClickListener(_ => saveClicked(editSetupPanel))
    val closeButton = new Button(Page.message("newSetupPage.cancelButton"))
    closeButton.addClickListener(_ => onDone())
    panelForEditor.addComponent(new HorizontalLayout(saveButton, closeButton))
    panelForEditor.setVisible(true)
  }

  def saveClicked(panel: ConnectorSetupPanel): Unit = {
    val maybeError = panel.validate
    if (maybeError.isEmpty) {
      configOperations.saveNewSetup(panel.getResult)
      onDone()
    } else {
      panel.showError(maybeError.get)
    }
  }
}
