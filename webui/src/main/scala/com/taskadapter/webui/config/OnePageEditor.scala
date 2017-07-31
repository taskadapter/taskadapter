package com.taskadapter.webui.config

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{UIConnectorConfig, UISyncConfig}
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui.{Alignment, Component, HorizontalLayout, VerticalLayout}

class OnePageEditor(messages: Messages, sandbox: Sandbox, config: UISyncConfig, exportToLeft: Runnable, exportToRight: Runnable) {
  val layout = new VerticalLayout

  layout.setWidth(760, com.vaadin.server.Sizeable.Unit.PIXELS)
  layout.setMargin(true)
  val connectorsLayout = new HorizontalLayout
  addConnectorPanel(connectorsLayout, config.getConnector1, sandbox, Alignment.MIDDLE_RIGHT)
  val exportButtonsFragment: Component = ExportButtonsFragment.render(messages, exportToLeft, exportToRight)
  connectorsLayout.addComponent(exportButtonsFragment)
  connectorsLayout.setComponentAlignment(exportButtonsFragment, Alignment.MIDDLE_CENTER)
  addConnectorPanel(connectorsLayout, config.getConnector2, sandbox, Alignment.MIDDLE_LEFT)
  layout.addComponent(connectorsLayout)
  val taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages, config.getConnector1, config.getConnector2, config.getNewMappings)
  layout.addComponent(taskFieldsMappingFragment.getUI)

  @throws[BadConfigException]
  def validate(): Unit = {
    // TODO !!! validate left/right editors too. this was lost during the last refactoring.
    taskFieldsMappingFragment.validate()
  }

  def removeEmptyRows : Unit = {
    taskFieldsMappingFragment.removeEmptyRows()
  }

  def getElements: Iterable[FieldMapping] = {
    taskFieldsMappingFragment.getElements
  }

  /**
    * Returns page editor UI.
    *
    * @return page editor UI.
    */
  def getUI: Component = layout

  private def addConnectorPanel(layout: HorizontalLayout, config: UIConnectorConfig, sandbox: Sandbox, align: Alignment) = {
    val miniPanel2 = createMiniPanel(config, sandbox)
    layout.addComponent(miniPanel2)
    layout.setComponentAlignment(miniPanel2, align)
  }

  private def createMiniPanel(connectorConfig: UIConnectorConfig, sandbox: Sandbox) = {
    val caption = "Configure " + connectorConfig.getLabel + "(" + config.connector1.getConnectorTypeId + ")"
    val connectorLabel = new MethodProperty[String](config, "label")
    val miniPanel = new MiniPanel(caption, connectorLabel)
    // "services" instance is only used by MSP Editor Factory
    miniPanel.setPanelContents(connectorConfig.createMiniPanel(sandbox))
    miniPanel
  }

}
