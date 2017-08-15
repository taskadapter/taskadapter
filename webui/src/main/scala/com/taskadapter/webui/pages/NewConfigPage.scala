package com.taskadapter.webui.pages

import com.taskadapter.PluginManager
import com.taskadapter.config.StorageException
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{ConfigId, SetupId}
import com.taskadapter.webui.ConfigOperations
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.service.EditorManager
import com.vaadin.ui._

trait Callback {
  /**
    * This method is called after new config was created.
    *
    * @param configId unique id of the new config.
    */
  def configCreated(configId: ConfigId): Unit
}

class NewConfigPage(editorManager: EditorManager, pluginManager: PluginManager, configOps: ConfigOperations,
                    sandbox: Sandbox, callback: Callback) {

  var stage = 1

  val panel = new Panel(message("createConfigPage.createNewConfig"))
  panel.setWidth("100%")
  val layout = new VerticalLayout()
  panel.setContent(layout)
  layout.setWidth("100%")

  var connector1Id: Option[String] = None
  var connector1SetupId: Option[SetupId] = None
  var connector2Id: Option[String] = None
  var connector2SetupId: Option[SetupId] = None
  var description: Option[String] = None

  refreshUI()

  def refreshUI(): Unit = {
    layout.removeAllComponents()
    layout.addComponent(getUIForCurrentStage())
  }

  def proceedToStage(newStage: Int): Unit = {
    stage = newStage
    refreshUI()
  }

  def getUIForCurrentStage(): Component = {
    stage match {
      case 1 => new SelectConnectorComponent(pluginManager, selected =
        (connectorId) => {
          connector1Id = Some(connectorId)
          proceedToStage(stage + 1)
        }
      ).ui

      case 2 => new NewConfigConfigureSystem(editorManager, configOps, sandbox, connector1Id.get, setupSelected =
        (label) => {
          connector1SetupId = Some(label)
          proceedToStage(stage + 1)
        }
      ).ui

      case 3 => new SelectConnectorComponent(pluginManager, selected =
        (connectorId) => {
          connector2Id = Some(connectorId)
          proceedToStage(stage + 1)
        }
      ).ui

      case 4 => new NewConfigConfigureSystem(editorManager, configOps, sandbox, connector2Id.get, setupSelected =
        (label) => {
          connector2SetupId = Some(label)
          proceedToStage(stage + 1)
        }
      ).ui

      case 5 => new NewConfigGiveDescription(d => {
        description = Some(d)
        saveClicked()
      }
      ).ui
    }
  }

  // empty label by default
  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")

  private def saveClicked() = {
    try {
      val configId = save()
      callback.configCreated(configId)
    } catch {
      case e: StorageException =>
        errorMessageLabel.setValue(message("createConfigPage.failedToSave"))
    }
  }

  @throws[StorageException]
  private def save(): ConfigId = {
    val descriptionString = description.get
    val id1 = connector1Id.get
    val id2 = connector2Id.get
    configOps.createNewConfig(descriptionString, id1, connector1SetupId.get, id2, connector2SetupId.get)
  }

}
