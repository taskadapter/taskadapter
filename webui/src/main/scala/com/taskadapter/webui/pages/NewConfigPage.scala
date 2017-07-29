package com.taskadapter.webui.pages

import com.taskadapter.PluginManager
import com.taskadapter.config.StorageException
import com.taskadapter.connector.definition.WebServerInfo
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.ConfigOperations
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.service.EditorManager
import com.vaadin.ui._

trait Callback {
  /**
    * This method is called after new config was created.
    *
    * @param config created config.
    */
  def configCreated(config: UISyncConfig): Unit
}

class NewConfigPage(editorManager: EditorManager, pluginManager: PluginManager, configOps: ConfigOperations, callback: Callback) {
  val connector1Info = new WebServerInfo
  val connector2Info = new WebServerInfo

  var stage = 1

  val panel = new Panel(message("createConfigPage.createNewConfig"))
  panel.setWidth("100%")
  val layout = new VerticalLayout()
  panel.setContent(layout)
  layout.setWidth("100%")

  var connector1Id: Option[String] = None
  var connector1Label: Option[String] = None
  var connector2Id: Option[String] = None
  var connector2Label: Option[String] = None
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
      case 1 => new NewConfigSelectSystem(pluginManager, selected =
        (connectorId) => {
          connector1Id = Some(connectorId)
          proceedToStage(stage + 1)
        }
      ).ui

      case 2 => new NewConfigConfigureSystem(editorManager, configOps, connector1Id.get, labelSelected =
        (label) => {
          connector1Label = Some(label)
          proceedToStage(stage + 1)
        }
      ).ui

      case 3 => new NewConfigSelectSystem(pluginManager, selected =
        (connectorId) => {
          connector2Id = Some(connectorId)
          proceedToStage(stage + 1)
        }
      ).ui

      case 4 => new NewConfigConfigureSystem(editorManager, configOps, connector2Id.get, labelSelected =
        (label) => {
          connector2Label = Some(label)
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
      val config = save()
      callback.configCreated(config)
    } catch {
      case e: StorageException =>
        errorMessageLabel.setValue(message("createConfigPage.failedToSave"))
    }
  }

  @throws[StorageException]
  private def save(): UISyncConfig = {
    val descriptionString = description.get
    val id1 = connector1Id.get
    val id2 = connector2Id.get
    configOps.createNewConfig(descriptionString, id1, connector1Label.get, id2, connector2Label.get)
  }

}
