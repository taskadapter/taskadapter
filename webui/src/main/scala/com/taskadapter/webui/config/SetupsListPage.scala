package com.taskadapter.webui.config

import com.taskadapter.PluginManager
import com.taskadapter.connector.definition.Descriptor
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui._

import scala.collection.JavaConverters._

class SetupsListPage(configOperations: ConfigOperations, editorManager: EditorManager, pluginManager: PluginManager,
                     sandbox: Sandbox) {
  val layout = new VerticalLayout
  layout.setSpacing(true)
  layout.setWidth(560, PIXELS)

  private val addButton = new Button(Page.message("setupsListPage.addButton"))
  addButton.addClickListener(_ => showAddBlock())

  private val plugins: Iterator[Descriptor] = pluginManager.getPluginDescriptors.asScala
  private val selectConnectorIdForNew = new ListSelect()
  selectConnectorIdForNew.setWidth("150px")
  selectConnectorIdForNew.setVisible(false)

  selectConnectorIdForNew.addValueChangeListener { event =>
    val connectorId = event.getProperty.getValue.toString
    showAddPanelForConnector(connectorId)
  }

  plugins.foreach { connector =>
    val itemId = selectConnectorIdForNew.addItem(connector.id)
    selectConnectorIdForNew.setItemCaption(itemId, connector.label)
  }
  selectConnectorIdForNew.setRows(plugins.size)

  val panelForEditor = new VerticalLayout()
  panelForEditor.setVisible(false)

  layout.addComponent(addButton)
  layout.addComponent(selectConnectorIdForNew)
  layout.addComponent(panelForEditor)

  val ui = layout

  val grid = new GridLayout(3, 5)
  grid.setSpacing(true)
  grid.setWidth("560px")
  layout.addComponent(grid)

  refresh()

  private def refresh(): Unit = {
    grid.removeAllComponents()
    addElements()
  }

  private def addElements(): Unit = {
    val setups = configOperations.getConnectorSetups()
    setups.foreach { setup =>
      val button = new Button(Page.message("setupsListPage.deleteButton"))
      button.addClickListener(_ => {
        configOperations.deleteConnectorSetup(SetupId(setup.id.get))
        refresh()
      })
      grid.addComponents(new Label(setup.connectorId),
        new Label(setup.label),
        button
      )
    }
  }

  def showAddBlock(): Unit = {
    selectConnectorIdForNew.setVisible(true)
    panelForEditor.setVisible(true)
  }

  def showAddPanelForConnector(connectorId: String): Unit = {
    val editor = editorManager.getEditorFactory(connectorId)
    val editSetupPanel = editor.getEditSetupPanel(sandbox)
    panelForEditor.removeAllComponents()
    panelForEditor.addComponent(editSetupPanel.getUI)
  }
}
