package com.taskadapter.webui.pages

import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.{ConfigActionsFragment, ConfigOperations, Page, Tracker}
import com.vaadin.ui._

/**
  * Config Summary panel with left/right arrows, connector names, action buttons (Delete/Clone/etc).
  */
object ConfigSummaryPanel {

  def render(config: UISyncConfig, mode: DisplayMode, callback: ConfigsPage.Callback,
             configOps: ConfigOperations, onExit: Runnable,
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