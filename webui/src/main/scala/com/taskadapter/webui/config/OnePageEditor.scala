package com.taskadapter.webui.config

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui.{ImageLoader, Page}
import com.vaadin.data.util.ObjectProperty
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme

class OnePageEditor(messages: Messages, sandbox: Sandbox, config: UISyncConfig, exportToLeft: Runnable, exportToRight: Runnable) {
  val layout = new VerticalLayout
  layout.setMargin(true)
  layout.setSpacing(true)
  layout.addComponent(createExportComponent())
  val taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages, config.getConnector1, config.getConnector2, config.getNewMappings)
  layout.addComponent(taskFieldsMappingFragment.getUI)

  @throws[BadConfigException]
  def validate(): Unit = {
    // TODO validate left/right editors too. this was lost during the last refactoring.
    taskFieldsMappingFragment.validate()
  }

  def removeEmptyRows(): Unit = {
    taskFieldsMappingFragment.removeEmptyRows()
  }

  def getElements: Iterable[FieldMapping] = {
    taskFieldsMappingFragment.getElements
  }

  def getUI: Component = layout

  private def createExportComponent(): Component = {
    val layout = new HorizontalLayout
    addConnectorPanel(layout, config.getConnector1, sandbox, Alignment.MIDDLE_RIGHT)
    val exportButtonsFragment = createExportButtonsFragment(messages, exportToLeft, exportToRight)
    layout.addComponent(exportButtonsFragment)
    layout.setComponentAlignment(exportButtonsFragment, Alignment.MIDDLE_CENTER)
    addConnectorPanel(layout, config.getConnector2, sandbox, Alignment.MIDDLE_LEFT)
    layout
  }

  def createExportButtonsFragment(messages: Messages, exportToLeft: Runnable, exportToRight: Runnable): Component = {
    val layout = new HorizontalLayout
    layout.setSpacing(true)
    layout.addComponent(createStartExportButton(messages, "arrow_left.png", exportToLeft))
    layout.addComponent(createStartExportButton(messages, "arrow_right.png", exportToRight))
    layout
  }

  private def createStartExportButton(messages: Messages, imageFile: String, handler: Runnable) = {
    val button = new Button
    button.setIcon(ImageLoader.getImage(imageFile))
    button.setDescription(messages.get("export.exportButtonTooltip"))
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.addClickListener(_ => handler.run())
    button.setWidth("100px")
    button
  }

  private def addConnectorPanel(layout: HorizontalLayout, config: UIConnectorConfig, sandbox: Sandbox, align: Alignment): Unit = {
    val button = createConfigureConnectorButton(config, sandbox)
    layout.addComponent(button)
    layout.setComponentAlignment(button, align)
  }

  private def createConfigureConnectorButton(connectorConfig: UIConnectorConfig, sandbox: Sandbox): Component = {
    val caption = Page.message("editConfig.configureConnector", connectorConfig.getLabel, connectorConfig.getConnectorTypeId)
    val labelProperty = new ObjectProperty[String](connectorConfig.getConnectorSetup.label)
    val iconResource = ImageLoader.getImage("edit.png")
    val button = new Button(connectorConfig.getLabel)
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.setIcon(iconResource)
    button.setWidth("350px")
    button.addClickListener(_ => showEditConnectorDialog(connectorConfig))
    button
  }

  def showEditConnectorDialog(connectorConfig: UIConnectorConfig): Unit = {
    val newWindow = new Window()

    newWindow.setContent(connectorConfig.createMiniPanel(sandbox))
    newWindow.center()
    newWindow.setModal(true)
    layout.getUI.addWindow(newWindow)
    newWindow.focus()
  }
}
