package com.taskadapter.webui

import com.taskadapter.config.StorageException
import com.taskadapter.web.PopupDialog
import com.taskadapter.web.uiapi.ConfigId
import com.vaadin.ui.{Button, HorizontalLayout, Notification}
import org.slf4j.LoggerFactory

/**
  * Contains buttons with various config actions. Shown on Config Summary page.
  *
  * @param configId  identity of the config to perform operations on.
  * @param configOps config operations.
  * @param onExit    exit request handler.
  */
class ConfigActionsFragment(configId: ConfigId, configOps: ConfigOperations, onExit: Runnable,
                            showPastExportResults: Runnable,
                            showLastResult: Runnable,
                            showConfigEditor: Runnable,
                            tracker: Tracker,
                            webUserSession: WebUserSession) {

  private val log = LoggerFactory.getLogger(classOf[ConfigActionsFragment])

  val layout = new HorizontalLayout
  layout.setSpacing(true)

  layout.addComponent(new Button(Page.message("configSummary.configure"), _ => showConfigEditor.run()))
  layout.addComponent(new Button(Page.message("configsPage.actionViewExportResults"), _ => showPastExportResults.run()))
  layout.addComponent(new Button(Page.message("configsPage.actionViewLastResult"), _ => showLastResult.run()))
  layout.addComponent(new Button(Page.message("configsPage.actionClone"), _ => showConfirmClonePage()))
  layout.addComponent(new Button(Page.message("configsPage.actionDelete"), _ => showDeleteConfigDialog()))

  private def showDeleteConfigDialog(): Unit = {
    val messageDialog = PopupDialog.confirm(Page.message("configsPage.actionDelete.confirmText"),
      () => {
        configOps.deleteConfig(configId)
        webUserSession.clearCurrentConfig()
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
