package com.taskadapter.webui.pages

import com.taskadapter.config.ConnectorSetup
import com.taskadapter.connector.definition.WebServerInfo
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.service.EditorManager
import com.taskadapter.webui.{ConfigOperations, Page}
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.ui._

class NewConfigConfigureSystem(editorManager: EditorManager, configOps: ConfigOperations, connectorId: String,
                               labelSelected: String => Unit) {

  def ui = createSetupPanelForConnector(new WebServerInfo()).getUI()

  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")

  private def createSetupPanelForConnector(connectorInfo: WebServerInfo): ChooseOrCreateSetupFragment = {
    val editor = editorManager.getEditorFactory(connectorId)

    val setups = configOps.getAllConnectorSetups(connectorId)
    val editSetupPanel = editor.getSetupPanel(connectorInfo)
    val addNewButton = new Button()
    new ChooseOrCreateSetupFragment(connectorInfo, setups, addNewButton, editSetupPanel)
  }

  class ChooseOrCreateSetupFragment(webServerInfo: WebServerInfo,
                                    setups: Seq[ConnectorSetup],
                                    button: Button, createPanel: Component) {
    private val selectPanel = createSavedServerConfigurationsSelector(message("createConfigPage.selectExistingOrNew"), setups,
      event => {}
    )

    private def createSavedServerConfigurationsSelector(title: String,
                                                        savedSetups: Seq[ConnectorSetup],
                                                        valueChangeListener: ValueChangeListener): ListSelect = {
      val res = new ListSelect(title)
      res.setNullSelectionAllowed(false)
      res.addValueChangeListener(valueChangeListener)
      savedSetups.foreach { s =>
        res.addItem(s.label)
        res.setItemCaption(s.label, s"${s.label} ${s.host}")
      }
      res.setRows(res.size)
      res
    }

    val layout = new VerticalLayout(selectPanel, button, createPanel, errorMessageLabel)
    if (selectPanel.getRows != 0) {
      selectPanel.select(selectPanel.getItemIds.iterator().next())
    }
    var inSelectMode = true

    def inEditMode: Boolean = !inSelectMode

    def getUI(): Component = layout

    val nextButton = new Button(Page.message("newConfig.next"))
    nextButton.addClickListener(_ => {
      val label = getLabel()
      if (inEditMode) {
        val maybeString = validateEditMode()
        if (maybeString.isEmpty) {
          configOps.saveSetup(getEditedResult(), label)
          labelSelected(label)
        } else {
          errorMessageLabel.setValue(maybeString.get)
        }
      } else {
        labelSelected(label)
      }
    }
    )
    layout.addComponent(nextButton)

    private def refresh() = {
      createPanel.setVisible(!inSelectMode)
      selectPanel.setVisible(inSelectMode)
      val caption = if (inSelectMode) Page.message("createConfigPage.button.createNew")
      else Page.message("createConfigPage.button.selectExisting")
      button.setCaption(caption)
    }

    def validateEditMode(): Option[String] = {
      val error = webServerInfo.validate()
      if (!error.isEmpty) {
        Some(error)
      } else None
    }

    def validateSelectMode(): Option[String] = {
      if (selectPanel.getValue == null) {
        Some(Page.message("createConfigPage.error.mustSelectOrCreate"))
      } else None
    }

    def getLabel(): String = {
      if (inSelectMode) {
        selectPanel.getValue.toString
      } else {
        webServerInfo.getLabel
      }
    }

    def getEditedResult(): WebServerInfo = webServerInfo

    button.addClickListener(_ => {
      inSelectMode = !inSelectMode
      refresh()
    })

    refresh()
  }

}
