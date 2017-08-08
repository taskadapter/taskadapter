package com.taskadapter.webui.pages

import com.taskadapter.connector.common.FileNameGenerator
import com.taskadapter.connector.definition.exception.SetupNameMissingException
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException
import com.taskadapter.connector.definition.{ConnectorSetup, FileSetup, WebConnectorSetup}
import com.taskadapter.web.ConnectorSetupPanel
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.ui._

import scala.collection.Seq

private case class ExistingSetup(id: SetupId, description: String)

class NewConfigConfigureSystem(editorManager: EditorManager, configOps: ConfigOperations,
                               sandbox: Sandbox,
                               connectorId: String,
                               setupSelected: SetupId => Unit) {

  def ui = createSetupPanelForConnector().getUI()

  val editor = editorManager.getEditorFactory(connectorId)

  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")

  private def createSetupPanelForConnector(): ChooseOrCreateSetupFragment = {

    val setups = configOps.getAllConnectorSetups(connectorId)
    val setupUiItems = setups.map { s =>
      if (s.isInstanceOf[WebConnectorSetup]) {
        val webSetup: WebConnectorSetup = s.asInstanceOf[WebConnectorSetup]
        ExistingSetup(SetupId(webSetup.id.get), s"${webSetup.connectorId} (${webSetup.host})")
      } else {
        val fileSetup: FileSetup = s.asInstanceOf[FileSetup]
        ExistingSetup(SetupId(fileSetup.id.get), fileSetup.label)
      }
    }

    val editSetupPanel = editor.getEditSetupPanel(sandbox)
    val addNewButton = new Button()
    new ChooseOrCreateSetupFragment(setupUiItems, addNewButton, editSetupPanel)
  }

  class ChooseOrCreateSetupFragment(setups: Seq[ExistingSetup],
                                       button: Button, connectorSetupPanel: ConnectorSetupPanel) {
    private val selectPanel = createSavedServerConfigurationsSelector(message("createConfigPage.selectExistingOrNew"), setups,
      event => {}
    )

    private def createSavedServerConfigurationsSelector(title: String,
                                                        savedSetups: Seq[ExistingSetup],
                                                        valueChangeListener: ValueChangeListener): ListSelect = {
      val res = new ListSelect(title)
      res.setNullSelectionAllowed(false)
      res.addValueChangeListener(valueChangeListener)
      savedSetups.foreach { s =>
        res.addItem(s.id)
        res.setItemCaption(s.id, s.description)
      }
      res.setRows(res.size)
      res
    }

    private val connectorSetupPanelUI = connectorSetupPanel.getUI
    val layout = new VerticalLayout(selectPanel, button, connectorSetupPanelUI, errorMessageLabel)
    if (selectPanel.getRows != 0) {
      selectPanel.select(selectPanel.getItemIds.iterator().next())
    }
    var inSelectMode = true

    def inEditMode: Boolean = !inSelectMode

    def getUI(): Component = layout

    val nextButton = new Button(Page.message("newConfig.next"))
    nextButton.addClickListener(_ => {
      val setupId = getSetupId()
      if (inEditMode) {
        val maybeString = validateEditMode()
        if (maybeString.isEmpty) {
          configOps.saveSetup(getEditedResult(), setupId)
          setupSelected(setupId)
        } else {
          errorMessageLabel.setValue(maybeString.get)
        }
      } else {
        setupSelected(setupId)
      }
    }
    )
    layout.addComponent(nextButton)

    private def refresh() = {
      connectorSetupPanelUI.setVisible(!inSelectMode)
      selectPanel.setVisible(inSelectMode)
      val caption = if (inSelectMode) Page.message("createConfigPage.button.createNew")
      else Page.message("createConfigPage.button.selectExisting")
      button.setCaption(caption)
    }

    def validateEditMode(): Option[String] = {
      try {
        connectorSetupPanel.validate
        None
      } catch {
        case e: SetupNameMissingException => Some(Page.message("newConfig.configure.nameRequired"))
        case e: ServerURLNotSetException => Some(Page.message("newConfig.configure.serverUrlRequired"))
        case e => Some(editor.formatError(e))
      }
    }

    def validateSelectMode(): Option[String] = {
      if (selectPanel.getValue == null) {
        Some(Page.message("createConfigPage.error.mustSelectOrCreate"))
      } else None
    }

    def getSetupId(): SetupId = {
      if (inSelectMode) {
        if (selectPanel.getValue != null) {
          selectPanel.getValue.asInstanceOf[SetupId]
        } else {
          throw new RuntimeException("unknown state")
        }
      } else {
        val newFile = FileNameGenerator.createSafeAvailableFile(configOps.getSavedSetupsFolder, connectorId + "_%d.json")
        SetupId(newFile.getName)
      }
    }

    def getEditedResult(): ConnectorSetup = connectorSetupPanel.getResult

    button.addClickListener(_ => {
      inSelectMode = !inSelectMode
      refresh()
    })

    refresh()
  }

}
