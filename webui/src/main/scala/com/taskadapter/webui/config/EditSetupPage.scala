package com.taskadapter.webui.config

import com.taskadapter.PluginManager
import com.taskadapter.connector.definition.ConnectorSetup
import com.taskadapter.web.PluginEditorFactory
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._

class EditSetupPage(configOperations: ConfigOperations, editorManager: EditorManager, pluginManager: PluginManager,
                    sandbox: Sandbox, setupId: SetupId, onDone: () => Unit) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  private val saveButton = new Button(Page.message("editSetupPage.saveButton"))
  saveButton.addClickListener(_ => saveClicked())

  private val closeButton = new Button(Page.message("editSetupPage.closeButton"))
  closeButton.addClickListener(_ => onDone())

  val ui = layout

  val setup : ConnectorSetup = configOperations.getSetup(setupId)

  val editor : PluginEditorFactory[_, ConnectorSetup]= editorManager.getEditorFactory(setup.connectorId)
  val editSetupPanel = editor.getEditSetupPanel(sandbox, Some(setup))
  layout.addComponent(editSetupPanel.getUI)

  layout.addComponent(new HorizontalLayout(saveButton, closeButton))

  def saveClicked(): Unit = {
    configOperations.saveSetup(editSetupPanel.getResult, setupId)
    onDone()
  }
}
