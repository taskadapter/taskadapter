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

  val panel = new Panel(message("createConfigPage.createNewConfig"))
  panel.setWidth("100%")
  val wizard = new WizardPanel()
  panel.setContent(wizard.ui)

  var connector1Id: Option[String] = None
  var connector1SetupId: Option[SetupId] = None
  var connector2Id: Option[String] = None
  var connector2SetupId: Option[SetupId] = None
  var description: Option[String] = None

  wizard.registerStep(1, new SelectConnectorWizardStep(pluginManager, next =
    (connectorId) => {
      connector1Id = Some(connectorId)
      wizard.showStep(2)
    }
  ))

  wizard.registerStep(2, new NewConfigConfigureSystem(editorManager, configOps, sandbox, setupSelected =
    (label) => {
      connector1SetupId = Some(label)
      wizard.showStep(3)
    }
  ))

  wizard.registerStep(3, new SelectConnectorWizardStep(pluginManager, next =
    (connectorId) => {
      connector2Id = Some(connectorId)
      wizard.showStep(4)
    }
  ))

  wizard.registerStep(4, new NewConfigConfigureSystem(editorManager, configOps, sandbox, setupSelected =
    (label) => {
      connector2SetupId = Some(label)
      wizard.showStep(5)
    }
  ))

  wizard.registerStep(5, new NewConfigGiveDescription(d => {
    description = Some(d)
    saveClicked()
  }
  ))

  wizard.showStep(1)

  // empty label by default
  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")

  private def saveClicked(): Unit = {
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
