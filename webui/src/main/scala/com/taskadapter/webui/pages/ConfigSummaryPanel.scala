package com.taskadapter.webui.pages

import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui._
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme

/**
  * Config Summary panel with left/right arrows, connector names, action buttons (Delete/Clone/etc).
  */
object ConfigSummaryPanel {

  def render(config: UISyncConfig, mode: DisplayMode, callback: ConfigsPage.Callback,
             configOps: ConfigOperations,
             sandbox: Sandbox,
             onExit: Runnable,
             showAllPreviousExportResults: Runnable,
             showLastExportResult: Runnable,
             tracker: Tracker): VerticalLayout = {
    val layout = new VerticalLayout
    layout.addStyleName("configPanelInConfigsList")
    layout.setSpacing(true)

    var descriptionButton = new Button(Page.message("configSummary.configure"))
    descriptionButton.setHtmlContentAllowed(true)
    descriptionButton.addClickListener(_ => callback.edit(config))

    val configOperationsBar = new ConfigActionsFragment(config.id, configOps, onExit,
      showAllPreviousExportResults, showLastExportResult, tracker).layout
    val buttonsLayout = new HorizontalLayout(descriptionButton, configOperationsBar)
    buttonsLayout.setSpacing(true)
    buttonsLayout.setExpandRatio(descriptionButton, 1)
    layout.addComponent(buttonsLayout)

    val horizontalLayout = new HorizontalLayout
    horizontalLayout.setSpacing(true)

    val leftSystemButton = createConfigureConnectorButton(layout, config.connector1, sandbox)
    horizontalLayout.addComponent(leftSystemButton)

    val leftRightButtonsPanel = new VerticalLayout()
    leftRightButtonsPanel.setSpacing(true)

    leftRightButtonsPanel.addComponent(createArrow("arrow_right.png", _ => callback.forwardSync(config)))
    leftRightButtonsPanel.addComponent(createArrow("arrow_left.png", _ => callback.backwardSync(config)))

    horizontalLayout.addComponent(leftRightButtonsPanel)

    val rightSystemButton = createConfigureConnectorButton(layout, config.connector2, sandbox)
    horizontalLayout.addComponent(rightSystemButton)

    layout.addComponent(horizontalLayout)
    layout
  }

  def createArrow(imageFileName: String, listener: ClickListener): Button = {
    val leftArrow = ImageLoader.getImage(imageFileName)
    val button = new Button(leftArrow)
    button.setHeight("40px")
    button.setWidth("100px")
    button.setDescription(Page.message("export.exportButtonTooltip"))
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.addClickListener(listener)
    button
  }

  private def createConfigureConnectorButton(layout: Layout, connectorConfig: UIConnectorConfig, sandbox: Sandbox): Component = {
    val iconResource = ImageLoader.getImage("edit.png")
    val button = new Button(connectorConfig.getLabel)
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.setIcon(iconResource)
    button.setWidth("270px")
    button.setHeight("100%")
    button.addClickListener(_ => showEditConnectorDialog(layout, connectorConfig, sandbox))
    button
  }

  private def showEditConnectorDialog(layout: Layout, connectorConfig: UIConnectorConfig, sandbox: Sandbox): Unit = {
    val newWindow = new Window()

    newWindow.setContent(connectorConfig.createMiniPanel(sandbox))
    newWindow.center()
    newWindow.setModal(true)
    layout.getUI.addWindow(newWindow)
    newWindow.focus()
  }

}