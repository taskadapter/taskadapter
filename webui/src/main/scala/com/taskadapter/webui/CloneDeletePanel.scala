package com.taskadapter.webui

import java.util

import com.taskadapter.config.StorageException
import com.taskadapter.web.MessageDialog
import com.taskadapter.web.uiapi.UISyncConfig
import com.vaadin.ui.{Button, HorizontalLayout, Notification}
import org.slf4j.LoggerFactory

/**
  * UI Component containing Clone and Delete buttons. Shown in "Edit Config"
  * page.
  *
  * @param config    current config.
  * @param configOps config operations.
  * @param onExit    exit request handler.
  */
final class CloneDeletePanel(config: UISyncConfig, configOps: ConfigOperations, onExit: Runnable, tracker: Tracker) {
  private val log = LoggerFactory.getLogger(classOf[CloneDeletePanel])
  private val YES = "Yes"
  private val CANCEL = "Cancel"

  val layout = new HorizontalLayout

  val cloneButton = new Button("Clone")
  cloneButton.setDescription("Clone this config")
  cloneButton.addClickListener(_ => showConfirmClonePage())
  layout.addComponent(cloneButton)
  val deleteButton = new Button("Delete")
  deleteButton.setDescription("Delete this config from Task Adapter")
  deleteButton.addClickListener(_ => showDeleteConfigDialog())
  layout.addComponent(deleteButton)

  private def showDeleteConfigDialog(): Unit = {
    val messageDialog = new MessageDialog("Confirmation", "Delete this config?", util.Arrays.asList(YES, CANCEL), (answer: String) => {
      def foo(answer: String) = {
        if (YES == answer) {
          configOps.deleteConfig(config)
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
          configOps.cloneConfig(config)
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
