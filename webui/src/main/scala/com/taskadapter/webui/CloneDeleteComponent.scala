package com.taskadapter.webui

import java.util

import com.taskadapter.config.StorageException
import com.taskadapter.web.MessageDialog
import com.taskadapter.web.uiapi.ConfigId
import com.vaadin.ui.{HorizontalLayout, MenuBar, Notification}
import org.slf4j.LoggerFactory

/**
  * Contains Clone and Delete elements. Shown on Configs List and Edit Config pages.
  *
  * @param configId identity of the config to perform operations on.
  * @param configOps config operations.
  * @param onExit    exit request handler.
  */
final class CloneDeleteComponent(configId: ConfigId, configOps: ConfigOperations, onExit: Runnable, tracker: Tracker) {

  private val log = LoggerFactory.getLogger(classOf[CloneDeleteComponent])
  private val YES = "Yes"
  private val CANCEL = "Cancel"

  val configOperationsBar = new MenuBar()
  var dropdown = configOperationsBar.addItem("", null)
  dropdown.addItem(Page.message("configsPage.actionClone"), (selectedItem: MenuBar#MenuItem) => showConfirmClonePage())
  dropdown.addItem(Page.message("configsPage.actionDelete"), (selectedItem: MenuBar#MenuItem) => showDeleteConfigDialog())

  val layout = new HorizontalLayout
  layout.addComponent(configOperationsBar)

  private def showDeleteConfigDialog(): Unit = {
    val messageDialog = new MessageDialog("Confirmation", "Delete this config?", util.Arrays.asList(YES, CANCEL), (answer: String) => {
      def foo(answer: String) = {
        if (YES == answer) {
          configOps.deleteConfig(configId)
          tracker.trackEvent("config", "deleted", "")
          onExit.run()
        }
      }

      foo(answer)
    })
    messageDialog.setWidth("175px")
    layout.getUI.addWindow(messageDialog)
  }

  def showConfirmClonePage(): Unit = {
    val messageDialog = new MessageDialog("Confirmation", "Clone this config?", util.Arrays.asList(YES, CANCEL), (answer: String) => {
      def foo(answer: String) = {
        if (YES == answer) try {
          configOps.cloneConfig(configId)
          onExit.run()
        } catch {
          case e: StorageException =>
            val message = "There were some troubles cloning the config:<BR>" + e.getMessage
            log.error(message, e)
            Notification.show(message, Notification.Type.ERROR_MESSAGE)
        }
      }

      foo(answer)
    })
    messageDialog.setWidth("175px")
    layout.getUI.addWindow(messageDialog)
  }
}
