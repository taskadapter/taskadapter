package com.taskadapter.webui.pages

import com.taskadapter.config.StorageException
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{ConfigId, SetupId}
import com.taskadapter.webui.{BasePage, ConfigOperations, EventTracker, Layout, SessionController}
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.service.Preservices
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.router.Route

trait Callback {
  /**
    * This method is called after new config was created.
    *
    * @param configId unique id of the new config.
    */
  def configCreated(configId: ConfigId): Unit
}

@Route(value = Navigator.NEW_CONFIG, layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class NewConfigPage extends BasePage {

  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()
  private val pluginManager = services.pluginManager
  private val editorManager = services.editorManager

  val wizard = new WizardPanel()

  var connector1Id: Option[String] = None
  var connector1SetupId: Option[SetupId] = None
  var connector2Id: Option[String] = None
  var connector2SetupId: Option[SetupId] = None
  var description: Option[String] = None

  wizard.registerStep(new SelectConnectorWizardStep(pluginManager, next =
    (connectorId) => {
      connector1Id = Some(connectorId)
      wizard.showStep(2)
    }
  ))

  wizard.registerStep(new NewConfigConfigureSystem(editorManager, configOps, sandbox, setupSelected =
    (label) => {
      connector1SetupId = Some(label)
      wizard.showStep(3)
    }
  ))

  wizard.registerStep(new SelectConnectorWizardStep(pluginManager, next =
    (connectorId) => {
      connector2Id = Some(connectorId)
      wizard.showStep(4)
    }
  ))

  wizard.registerStep(new NewConfigConfigureSystem(editorManager, configOps, sandbox, setupSelected =
    (label) => {
      connector2SetupId = Some(label)
      wizard.showStep(5)
    }
  ))

  wizard.registerStep(new NewConfigGiveDescription(d => {
    description = Some(d)
    saveClicked()
  }
  ))

  wizard.showStep(1)

  // empty label by default
  val errorMessageLabel = new Text("")
//  errorMessageLabel.addClassName("error-message-label")

  add(new Label(message("createConfigPage.createNewConfig")),
    errorMessageLabel,
    wizard.ui)

  private def saveClicked(): Unit = {
    try {
      val configId = save()
      Navigator.configsList()
    } catch {
      case e: StorageException =>
        errorMessageLabel.setText(message("createConfigPage.failedToSave"))
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
