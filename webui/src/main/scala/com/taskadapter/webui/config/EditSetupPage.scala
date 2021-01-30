package com.taskadapter.webui.config

import com.taskadapter.connector.definition.ConnectorSetup
import com.taskadapter.web.PluginEditorFactory
import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.pages.Navigator
import com.taskadapter.webui.{BasePage, EventTracker, Layout, Page, SessionController}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.{BeforeEvent, HasUrlParameter, Route}

@Route(value = "edit-setup", layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class EditSetupPage() extends BasePage with HasUrlParameter[String]{

  private val configOps = SessionController.buildConfigOperations()
  private val services = SessionController.getServices
  private val sandbox = SessionController.createSandbox()

  override def setParameter(event: BeforeEvent, setupIdStr: String): Unit = {
    showSetup(SetupId(setupIdStr))
  }

  def showSetup(setupId: SetupId) : Unit = {

    val setup: ConnectorSetup = configOps.getSetup(setupId)

    val editor: PluginEditorFactory[_, ConnectorSetup] = services.editorManager.getEditorFactory(setup.connectorId)
    val editSetupPanel = editor.getEditSetupPanel(sandbox, setup)
    add(editSetupPanel.getUI)

    def saveClicked(): Unit = {
      val maybeError = editSetupPanel.validate
      if (maybeError.isEmpty) {
        configOps.saveSetup(editSetupPanel.getResult, setupId)
        Navigator.setupsList()
      } else {
        editSetupPanel.showError(maybeError.get)
      }
    }

    val saveButton = new Button(Page.message("editSetupPage.saveButton"))
    saveButton.addClickListener(_ => saveClicked())

    val closeButton = new Button(Page.message("editSetupPage.closeButton"))
    closeButton.addClickListener(_ => Navigator.setupsList())


    add(new HorizontalLayout(saveButton, closeButton))

  }
}
