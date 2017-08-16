package com.taskadapter.webui.config

import com.taskadapter.config.StorageException
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui._
import com.taskadapter.webui.data.ExceptionFormatter
import com.vaadin.data.util.ObjectProperty
import com.vaadin.server.Sizeable.Unit.PERCENTAGE
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.slf4j.LoggerFactory

class EditConfigPage(messages: Messages, tracker: Tracker,
                     configOps: ConfigOperations,
                     sandbox: Sandbox, config: UISyncConfig, exportToLeft: Runnable,
                     exportToRight: Runnable,
                     close: Runnable) {
  private val logger = LoggerFactory.getLogger(classOf[EditConfigPage])

  val descriptionFieldWidth = "600px"
  val descriptionFormWidth = "700px"

  val labelProperty = new ObjectProperty[String](config.label)

  val layout = new VerticalLayout
  layout.setSpacing(true)
  //  val goToConfigsListbutton = new Button(Page.message("editConfig.goToConfigsList"))
  //  goToConfigsListbutton.setDescription(Page.message("editConfig.goToConfigsList.tooltip"))
  //  goToConfigsListbutton.addClickListener(_ => callback.back())

  val editDescriptionForm = createEditDescriptionElement(config)
  editDescriptionForm.setWidth(descriptionFormWidth)

  var errorMessageLabel = new Label()
  errorMessageLabel.addStyleName("error-message-label")
  errorMessageLabel.setWidth(100, PERCENTAGE)
  errorMessageLabel.setContentMode(ContentMode.HTML)
  errorMessageLabel.setVisible(false)

  val buttons = createConfigOperationsButtons
  buttons.setWidth("20%")
  //  layout.addComponent(goToConfigsListbutton)
  val topRowLayout = new HorizontalLayout(editDescriptionForm, buttons)
  topRowLayout.setComponentAlignment(editDescriptionForm, Alignment.MIDDLE_LEFT)
  topRowLayout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT)
  topRowLayout.setExpandRatio(editDescriptionForm, 1)

  layout.addComponent(topRowLayout)

  layout.addComponent(createExportComponent())
  layout.addComponent(errorMessageLabel)

  val taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages, config.getConnector1, config.getConnector2, config.getNewMappings)
  layout.addComponent(taskFieldsMappingFragment.getUI)

  def removeEmptyRows(): Unit = {
    taskFieldsMappingFragment.removeEmptyRows()
  }

  def getElements: Iterable[FieldMapping] = {
    taskFieldsMappingFragment.getElements
  }

  def getUI: Component = layout

  private def createExportComponent(): Component = {
    val layout = new HorizontalLayout
    addConnectorPanel(layout, config.getConnector1, sandbox, Alignment.MIDDLE_RIGHT)
    val exportButtonsFragment = createExportButtonsFragment(messages, exportToLeft, exportToRight)
    layout.addComponent(exportButtonsFragment)
    layout.setComponentAlignment(exportButtonsFragment, Alignment.MIDDLE_CENTER)
    addConnectorPanel(layout, config.getConnector2, sandbox, Alignment.MIDDLE_LEFT)
    layout
  }

  def createExportButtonsFragment(messages: Messages, exportToLeft: Runnable, exportToRight: Runnable): Component = {
    val layout = new HorizontalLayout
    layout.setSpacing(true)
    layout.addComponent(createStartExportButton(messages, "arrow_left.png", exportToLeft))
    layout.addComponent(createStartExportButton(messages, "arrow_right.png", exportToRight))
    layout
  }

  private def createStartExportButton(messages: Messages, imageFile: String, handler: Runnable) = {
    val button = new Button
    button.setIcon(ImageLoader.getImage(imageFile))
    button.setDescription(messages.get("export.exportButtonTooltip"))
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.addClickListener(_ => handler.run())
    button.setWidth("100px")
    button
  }

  private def addConnectorPanel(layout: HorizontalLayout, config: UIConnectorConfig, sandbox: Sandbox, align: Alignment): Unit = {
    val button = createConfigureConnectorButton(config, sandbox)
    layout.addComponent(button)
    layout.setComponentAlignment(button, align)
  }

  private def createConfigureConnectorButton(connectorConfig: UIConnectorConfig, sandbox: Sandbox): Component = {
    val caption = Page.message("editConfig.configureConnector", connectorConfig.getLabel, connectorConfig.getConnectorTypeId)
    val labelProperty = new ObjectProperty[String](connectorConfig.getConnectorSetup.label)
    val iconResource = ImageLoader.getImage("edit.png")
    val button = new Button(connectorConfig.getLabel)
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.setIcon(iconResource)
    button.setWidth("350px")
    button.addClickListener(_ => showEditConnectorDialog(connectorConfig))
    button
  }

  def showEditConnectorDialog(connectorConfig: UIConnectorConfig): Unit = {
    val newWindow = new Window()

    newWindow.setContent(connectorConfig.createMiniPanel(sandbox))
    newWindow.center()
    newWindow.setModal(true)
    layout.getUI.addWindow(newWindow)
    newWindow.focus()
  }


  private def createConfigOperationsButtons = {
    val buttonsLayout = new HorizontalLayout
    buttonsLayout.setWidth(100, PERCENTAGE)
    val rightLayout = new HorizontalLayout
    rightLayout.setSpacing(true)
    buttonsLayout.addComponent(rightLayout)
    buttonsLayout.setComponentAlignment(rightLayout, Alignment.BOTTOM_RIGHT)
    val saveButton = new Button(Page.message("button.save"))
    saveButton.addClickListener(_ => saveClicked())
    rightLayout.addComponent(saveButton)
    val backButton = new Button(Page.message("button.close"))
    backButton.addClickListener(_ => close.run())

    val cloneDeletePanel= new CloneDeleteComponent(config.id, configOps, close, tracker).layout
    rightLayout.addComponent(backButton)
    rightLayout.addComponent(cloneDeletePanel)
    buttonsLayout
  }

  private def saveClicked(): Unit = {
    if (validate) {
      save()
      Notification.show("", Page.message("editConfig.messageSaved"), Notification.Type.HUMANIZED_MESSAGE)
    }
  }

  private def validate: Boolean = {
    clearErrorMessage()
    try
      // TODO validate left/right editors too. this was lost during the last refactoring.
      taskFieldsMappingFragment.validate()
    catch {
      case e: FieldAlreadyMappedException =>
        val s = Page.message("editConfig.error.fieldAlreadyMapped", e.getValue)
        showError(s)
        return false
      case e: FieldNotMappedException =>
        val s = Page.message("error.fieldSelectedForExportNotMapped", e.fieldName)
        showError(s)
        return false
      case e: BadConfigException =>
        val s = ExceptionFormatter.format(e)
        showError(s)
        return false
    }
    true
  }

  private def save(): Unit = {
    try {
      removeEmptyRows()
      val newFieldMappings = getElements.toSeq
      val newConfig = config.copy(fieldMappings = newFieldMappings,
        label = labelProperty.getValue
      )
      configOps.saveConfig(newConfig)
      tracker.trackEvent("config", "saved", "")
    } catch {
      case e: StorageException =>
        val message = Page.message("editConfig.error.cantSave", e.getMessage)
        showError(message)
        logger.error(message, e)
    }
  }

  private def createEditDescriptionElement(config: UISyncConfig) : Component = {
    val form = new FormLayout
    val descriptionField = new TextField(Page.message("editConfig.description"))
    descriptionField.setWidth(descriptionFieldWidth)
    descriptionField.setPropertyDataSource(labelProperty)
    form.addComponent(descriptionField)
    form
  }

  def showError(errorMessage: String): Unit = {
    errorMessageLabel.setVisible(true)
    errorMessageLabel.setValue(errorMessage)
  }
  def clearErrorMessage(): Unit = {
    errorMessageLabel.setVisible(false)
    errorMessageLabel.setValue("")
  }
}
