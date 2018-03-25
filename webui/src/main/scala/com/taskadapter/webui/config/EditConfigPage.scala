package com.taskadapter.webui.config

import com.google.common.base.Strings
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
                     error: String,
                     sandbox: Sandbox, config: UISyncConfig, exportToLeft: Runnable,
                     exportToRight: Runnable,
                     close: Runnable,
                     showAllPreviousResults: Runnable,
                     showLastResults: Runnable) {
  private val logger = LoggerFactory.getLogger(classOf[EditConfigPage])

  val labelProperty = new ObjectProperty[String](config.label)

  val layout = new VerticalLayout
  layout.setSpacing(true)

  val editDescriptionForm = createEditDescriptionElement(config)
  editDescriptionForm.setWidth(Sizes.editConfigDescriptionFormWidth)

  var errorMessageLabel = new Label()
  errorMessageLabel.addStyleName("error-message-label")
  errorMessageLabel.setWidth(100, PERCENTAGE)
  errorMessageLabel.setContentMode(ContentMode.HTML)
  errorMessageLabel.setVisible(false)

  if (!Strings.isNullOrEmpty(error)) {
    showError(error)
  }
  val buttons = createConfigOperationsButtons
  buttons.setWidth("20%")
  val topRowLayout = new HorizontalLayout(editDescriptionForm, buttons)
  topRowLayout.setComponentAlignment(editDescriptionForm, Alignment.MIDDLE_LEFT)
  topRowLayout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT)
  topRowLayout.setExpandRatio(editDescriptionForm, 1)

  layout.addComponent(topRowLayout)

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

    val cloneDeletePanel = new ConfigActionsFragment(config.id, configOps, close, showAllPreviousResults,
      showLastResults,
      tracker).layout
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

  private def createEditDescriptionElement(config: UISyncConfig): Component = {
    val form = new FormLayout
    val descriptionField = new TextField(Page.message("editConfig.description"))
    descriptionField.setWidth(Sizes.editConfigDescriptionFieldWidth)
    descriptionField.setPropertyDataSource(labelProperty)
    // can use this to auto-save field changes. don't want to do this for just one field though.
    // need to be the same experience for all fields.
//    descriptionField.addBlurListener(_ => save())
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
