package com.taskadapter.webui.pages

import com.taskadapter.connector.definition.{ConnectorConfig, ConnectorSetup, FileSetup, WebConnectorSetup}
import com.taskadapter.web.{ConnectorSetupPanel, PluginEditorFactory}
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
                               setupSelected: SetupId => Unit) extends WizardStep[SetupId] {
  var result: SetupId = _

  def ui(connectorId: Any): Component = {
    createSetupPanelForConnector(connectorId.asInstanceOf[String]).getUI()
  }

  private def createSetupPanelForConnector(connectorId: String): ChooseOrCreateSetupFragment = {

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

    // if you remove these class declarations, you will get runtime ClassCastExceptions saying cannot convert
    // WebConnectorSetup to Nothing!
    val editor: PluginEditorFactory[ConnectorConfig, ConnectorSetup] = editorManager.getEditorFactory(connectorId)
    val setup: ConnectorSetup = editor.createDefaultSetup()
    val editSetupPanel = editor.getEditSetupPanel(sandbox, setup)
    val addNewButton = new Button()
    new ChooseOrCreateSetupFragment(setupUiItems, addNewButton, editSetupPanel)
  }

  class ChooseOrCreateSetupFragment(setups: Seq[ExistingSetup],
                                    button: Button, connectorSetupPanel: ConnectorSetupPanel) {
    private val selectPanel = createSavedServerConfigurationsSelector(setups, event => {})

    private def createSavedServerConfigurationsSelector(savedSetups: Seq[ExistingSetup],
                                                        valueChangeListener: ValueChangeListener): ListSelect = {
      val res = new ListSelect()
      res.setNullSelectionAllowed(false)
      res.addValueChangeListener(valueChangeListener)
      savedSetups.foreach { s =>
        res.addItem(s.id)
        res.setItemCaption(s.id, s.description)
      }
      res.setRows(res.size)
      res
    }

    val errorMessageLabel = new Label
    errorMessageLabel.addStyleName("error-message-label")

    private val connectorSetupPanelUI = connectorSetupPanel.getUI
    val selectExistingLabel = new Label(message("createConfigPage.selectExistingOrNew"))
    val orCreateNewLabel = new Label(message("createConfigPage.orCreateNew"))
    val layout = new VerticalLayout(selectExistingLabel, selectPanel, orCreateNewLabel, button, connectorSetupPanelUI, errorMessageLabel)
    var inSelectMode = true
    if (selectPanel.getRows != 0) {
      selectPanel.select(selectPanel.getItemIds.iterator().next())
    }
    if (selectPanel.getRows == 0) {
      inSelectMode = false
    }

    def inEditMode: Boolean = !inSelectMode

    def getUI(): Component = layout

    val nextButton = new Button(Page.message("newConfig.next"))
    nextButton.addClickListener(_ => {
      if (inEditMode) {
        val maybeString = connectorSetupPanel.validate
        if (maybeString.isEmpty) {
          val setupId = configOps.saveNewSetup(connectorSetupPanel.getResult)
          result = setupId
          setupSelected(setupId)
        } else {
          errorMessageLabel.setValue(maybeString.get)
        }
      } else {
        val setupId = selectPanel.getValue.asInstanceOf[SetupId]
        result = setupId
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
