package com.taskadapter.webui

import com.taskadapter.config.StorageException
import com.taskadapter.web.PopupDialog
import com.taskadapter.web.uiapi.ConfigId
import com.taskadapter.webui.pages.Navigator
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.notification.{Notification, NotificationVariant}
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import org.slf4j.LoggerFactory

/**
  * Contains buttons with various config actions (Clone, Delete, etc). Shown on Config Summary page.
  *
  * @param configId  identity of the config to perform operations on.
  */
class ConfigActionsFragment(configId: ConfigId) extends HorizontalLayout {

  private val configOps: ConfigOperations = SessionController.buildConfigOperations()

  private val log = LoggerFactory.getLogger(classOf[ConfigActionsFragment])

  add(new Button(Page.message("configsPage.actionClone"), _ => showConfirmClonePage()))
  add(new Button(Page.message("configsPage.actionDelete"), _ => showDeleteConfigDialog()))

  private def showDeleteConfigDialog(): Unit = {
    PopupDialog.confirm(Page.message("configsPage.actionDelete.confirmText"),
      () => {
        configOps.deleteConfig(configId)
        Navigator.configsList()
      }
    )
  }

  def showConfirmClonePage(): Unit = {
   PopupDialog.confirm(Page.message("configsPage.actionClone.confirmText"),
      () => {
        try {
          configOps.cloneConfig(configId)
          Navigator.configsList()
          Notification.show(Page.message("configsPage.actionClone.success"))
            .addThemeVariants(NotificationVariant.LUMO_SUCCESS)
        } catch {
          case e: StorageException =>
            val message = Page.message("configsPage.actionClone.error", e.getMessage)
            log.error(message, e)
            Notification.show(message)
              .addThemeVariants(NotificationVariant.LUMO_ERROR)
        }
      }
    )
  }
}
