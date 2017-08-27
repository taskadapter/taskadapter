package com.taskadapter.webui

import com.taskadapter.config.StorageException
import com.taskadapter.web.PopupDialog
import com.taskadapter.web.uiapi.ConfigId
import com.vaadin.ui.{HorizontalLayout, MenuBar, Notification}
import org.slf4j.LoggerFactory

/**
  * Contains Clone and Delete elements. Shown on Configs List and Edit Config pages.
  *
  * @param configId  identity of the config to perform operations on.
  * @param configOps config operations.
  * @param onExit    exit request handler.
  */
class CloneDeleteComponent(configId: ConfigId, configOps: ConfigOperations, onExit: Runnable,
                           showPastExportResults:Runnable,
                           tracker: Tracker) {

  private val log = LoggerFactory.getLogger(classOf[CloneDeleteComponent])

  val configOperationsBar = new MenuBar()
  var dropdown = configOperationsBar.addItem("", null)
  dropdown.addItem(Page.message("configsPage.actionClone"), (selectedItem: MenuBar#MenuItem) => showConfirmClonePage())
  dropdown.addItem(Page.message("configsPage.actionDelete"), (selectedItem: MenuBar#MenuItem) => showDeleteConfigDialog())
  dropdown.addItem(Page.message("configsPage.actionViewExportResults"), (selectedItem: MenuBar#MenuItem) => showPastExportResults.run())

  val layout = new HorizontalLayout
  layout.addComponent(configOperationsBar)

  private def showDeleteConfigDialog(): Unit = {
    val messageDialog = PopupDialog.confirm(Page.message("configsPage.actionDelete.confirmText"),
      () => {
        configOps.deleteConfig(configId)
        tracker.trackEvent("config", "deleted", "")
        onExit.run()
      }
    )
    layout.getUI.addWindow(messageDialog)
  }

  def showConfirmClonePage(): Unit = {
    val messageDialog = PopupDialog.confirm(Page.message("configsPage.actionClone.confirmText"),
      () => {
        try {
          configOps.cloneConfig(configId)
          onExit.run()
        } catch {
          case e: StorageException =>
            val message = "There were some troubles cloning the config:<BR>" + e.getMessage
            log.error(message, e)
            Notification.show(message, Notification.Type.ERROR_MESSAGE)
        }
      }
    )
    layout.getUI.addWindow(messageDialog)
  }
}
