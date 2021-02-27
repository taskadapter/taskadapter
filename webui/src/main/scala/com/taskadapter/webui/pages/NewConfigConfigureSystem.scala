package com.taskadapter.webui.pages

import com.taskadapter.connector.definition.{ConnectorConfig, ConnectorSetup, FileSetup, WebConnectorSetup}
import com.taskadapter.web.{ConnectorSetupPanel, PluginEditorFactory}
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.SetupId
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select

import scala.collection.Seq

private case class ExistingSetup(id: SetupId, description: String)

class NewConfigConfigureSystem(editorManager: EditorManager, configOps: ConfigOperations,
                               sandbox: Sandbox,
                               setupSelected: SetupId => Unit) extends WizardStep[SetupId] {
  var result: SetupId = _

  @Override
  def ui(connectorId: Any): Component = {
    createSetupPanelForConnector(connectorId.asInstanceOf[String]).getUI()
  }

  private def createSetupPanelForConnector(connectorId: String): ChooseOrCreateSetupFragment = {

    val setups = configOps.getAllConnectorSetups(connectorId)
    val setupUiItems = setups.map { s =>
      if (s.isInstanceOf[WebConnectorSetup]) {
        val webSetup: WebConnectorSetup = s.asInstanceOf[WebConnectorSetup]
        ExistingSetup(SetupId(webSetup.getId), s"${webSetup.getConnectorId} (${webSetup.getHost})")
      } else {
        val fileSetup: FileSetup = s.asInstanceOf[FileSetup]
        ExistingSetup(SetupId(fileSetup.getId), fileSetup.getLabel)
      }
    }

    // if you remove these class declarations, you will get runtime ClassCastExceptions saying cannot convert
    // WebConnectorSetup to Nothing!
    val editor: PluginEditorFactory[ConnectorConfig, ConnectorSetup] = editorManager.getEditorFactory(connectorId)
    val setup: ConnectorSetup = editor.createDefaultSetup(sandbox)
    val editSetupPanel = editor.getEditSetupPanel(sandbox, setup)
    val addNewButton = new Button()
    new ChooseOrCreateSetupFragment(setupUiItems, addNewButton, editSetupPanel)
  }

  class ChooseOrCreateSetupFragment(setups: Seq[ExistingSetup],
                                    button: Button, connectorSetupPanel: ConnectorSetupPanel) {
    private val selectPanel = createSavedServerConfigurationsSelector(setups)
    val selectExistingLabel = message("createConfigPage.selectExistingOrNew")

    private def createSavedServerConfigurationsSelector(savedSetups: Seq[ExistingSetup]): Select[ExistingSetup] = {
      val combobox = new Select[ExistingSetup]()
      combobox.setMinWidth("500px")
      combobox.setLabel(selectExistingLabel)
      combobox.setEmptySelectionAllowed(false)
      combobox.setItemLabelGenerator((item: ExistingSetup) => item.description)
      combobox.setItems(java.util.Arrays.asList(savedSetups: _*))
      combobox
    }

    val errorMessageLabel = new Label
    errorMessageLabel.addClassName("error-message-label")

    private val connectorSetupPanelUI = connectorSetupPanel.getComponent
    val orCreateNewLabel = new Label(message("createConfigPage.orCreateNew"))
    var inSelectMode = true

    if (setups.nonEmpty) {
      selectPanel.setValue(setups.head)
    } else {
      inSelectMode = false
      button.setEnabled(false)
    }

    def inEditMode: Boolean = !inSelectMode

    val nextButton = new Button(Page.message("newConfig.next"))
    nextButton.addClickListener(_ => {
      if (inEditMode) {
        val maybeString = connectorSetupPanel.validate
        if (maybeString.isEmpty) {
          val setupId = configOps.saveNewSetup(connectorSetupPanel.getResult)
          result = setupId
          setupSelected(setupId)
        } else {
          errorMessageLabel.setText(maybeString.get)
        }
      } else {
        val setupId = selectPanel.getValue.id
        result = setupId
        setupSelected(setupId)
      }
    }
    )

    val layout = new VerticalLayout()
    layout.add(selectPanel, orCreateNewLabel, button, connectorSetupPanelUI, errorMessageLabel,
      nextButton)

    def getUI(): Component = layout

    private def refresh() = {
      connectorSetupPanelUI.setVisible(!inSelectMode)
      selectPanel.setVisible(inSelectMode)
      val caption = if (inSelectMode) Page.message("createConfigPage.button.createNew")
      else Page.message("createConfigPage.button.selectExisting")
      button.setText(caption)
    }

    def validateSelectMode(): Option[String] = {
      if (selectPanel.getValue == null) {
        Some(Page.message("createConfigPage.error.mustSelectOrCreate"))
      } else None
    }

    button.addClickListener(_ => {
      inSelectMode = !inSelectMode
      refresh()
    })

    refresh()
  }

  override def getResult: SetupId = result
}
