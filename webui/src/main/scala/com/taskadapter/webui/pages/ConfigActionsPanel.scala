package com.taskadapter.webui.pages

import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.{CloneDeleteComponent, ConfigOperations, Page, Tracker}
import com.vaadin.ui._

/**
  * Buttons panel with left/right arrows.
  */
object ConfigActionsPanel {
  def render(config: UISyncConfig, mode: ConfigsPage.DisplayMode, callback: ConfigsPage.Callback,
             configOps: ConfigOperations, onExit: Runnable, tracker: Tracker): Component = {
    val layout = new VerticalLayout
    layout.addStyleName("configPanelInConfigsList")
    val labelText = mode.nameOf(config)
    val description = if (labelText.isEmpty) Page.message("configsPage.noDescription") else labelText

    var descriptionButton = new Button(description)
    descriptionButton.setWidth("500px")
    descriptionButton.setHtmlContentAllowed(true)
    descriptionButton.addClickListener(_ => callback.edit(config))

    val configOperationsBar = new CloneDeleteComponent(config.id, configOps, onExit, tracker).layout
    val descriptionLayout = new HorizontalLayout(descriptionButton, configOperationsBar)
    descriptionLayout.setExpandRatio(descriptionButton, 1)
    layout.addComponent(descriptionLayout)

    val horizontalLayout = new HorizontalLayout
    horizontalLayout.setSpacing(true)
    horizontalLayout.addComponent(UniConfigExport.render(config, new UniConfigExport.Callback() {
      override def dropInExport(file: Html5File) = callback.forwardDropIn(config, file)

      override def doExport() = callback.forwardSync(config)
    }))
    horizontalLayout.addComponent(UniConfigExport.render(config.reverse, new UniConfigExport.Callback() {
      override def dropInExport(file: Html5File) = callback.backwardDropIn(config, file)

      override def doExport() = callback.backwardSync(config)
    }))
    layout.addComponent(horizontalLayout)
    layout
  }
}