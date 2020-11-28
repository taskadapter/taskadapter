package com.taskadapter.webui

import com.taskadapter.config.StorageException
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.web.PopupDialog
import com.taskadapter.web.event.{ConfigCloneRequested, ConfigDeleteRequested, EventBusImpl, ShowConfigsListPageRequested}
import com.taskadapter.web.uiapi.ConfigId
import com.vaadin.ui.{Button, Notification}
import org.slf4j.LoggerFactory

/**
  * Contains buttons with various config actions. Shown on Config Summary page.
  *
  * @param configId  identity of the config to perform operations on.
  */
class ConfigActionsFragment(configId: ConfigId) {

  private val log = LoggerFactory.getLogger(classOf[ConfigActionsFragment])

  val layout = new HorizontalLayout
  layout.setSpacing(true)

  layout.add(new Button(Page.message("configsPage.actionClone"), _ => showConfirmClonePage()))
  layout.add(new Button(Page.message("configsPage.actionDelete"), _ => showDeleteConfigDialog()))

  private def showDeleteConfigDialog(): Unit = {
    val messageDialog = PopupDialog.confirm(Page.message("configsPage.actionDelete.confirmText"),
      () => {
        EventBusImpl.post(ConfigDeleteRequested(configId))
        EventBusImpl.post(ShowConfigsListPageRequested())
      }
    )
    layout.getUI.addWindow(messageDialog)
  }

  def showConfirmClonePage(): Unit = {
    val messageDialog = PopupDialog.confirm(Page.message("configsPage.actionClone.confirmText"),
      () => {
        try {
          EventBusImpl.post(ConfigCloneRequested(configId))
          EventBusImpl.post(ShowConfigsListPageRequested())
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
