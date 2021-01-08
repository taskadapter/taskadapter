package com.taskadapter.webui.config

import com.google.common.base.Strings
import com.taskadapter.config.StorageException
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exception.FieldNotMappedException
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.configeditor.EditorUtil
import com.taskadapter.web.data.Messages
import com.taskadapter.web.ui.HtmlLabel
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui._
import com.taskadapter.webui.data.ExceptionFormatter
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.data.binder.Binder
import org.slf4j.LoggerFactory

/**
  * Fields mapping plus "description" element at the top.
  */
class EditConfigPage(configOps: ConfigOperations,
                     messages: Messages,
                     error: String,
                     config: UISyncConfig) {
  private val logger = LoggerFactory.getLogger(classOf[EditConfigPage])

  val binder = new Binder[UISyncConfig](classOf[UISyncConfig])
  val descriptionField = EditorUtil.textInput(binder, "label")
  val editDescriptionForm = createEditDescriptionElement()

  var errorMessageLabel = new HtmlLabel("")
  errorMessageLabel.addClassName("error-message-label")
  errorMessageLabel.setWidth("100%")
  errorMessageLabel.setVisible(false)

  if (!Strings.isNullOrEmpty(error)) {
    showError(error)
  }
  val buttons = createConfigOperationsButtons
  buttons.setWidth("20%")
  val topRowLayout = new HorizontalLayout(editDescriptionForm, buttons)

  val layout = new VerticalLayout(topRowLayout, errorMessageLabel)

  val taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages,
    config.getConnector1.getAllFields, config.getConnector1.fieldNames, config.getConnector1.getLabel,
    config.getConnector2.getAllFields, config.getConnector2.fieldNames, config.getConnector2.getLabel,
    config.getNewMappings)

  layout.add(taskFieldsMappingFragment.getComponent)

  binder.readBean(config)

  def getElements: Iterable[FieldMapping[_]] = {
    taskFieldsMappingFragment.getElements
  }

  def getUI: Component = layout

  private def createConfigOperationsButtons = {
    val buttonsLayout = new HorizontalLayout
    val rightLayout = new HorizontalLayout
    rightLayout.setSpacing(true)
    buttonsLayout.add(rightLayout)
    val saveButton = new Button(Page.message("button.save"))
    saveButton.addClickListener(_ => saveClicked())
    rightLayout.add(saveButton)
    val backButton = new Button(Page.message("button.close"))
//    backButton.addClickListener(_ => close.run())

    rightLayout.add(backButton)
    buttonsLayout
  }

  private def saveClicked(): Unit = {
    if (validate) {
      save()
      Notification.show(Page.message("editConfig.messageSaved"))
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
      taskFieldsMappingFragment.save()
      val newFieldMappings = getElements.toSeq
      val newConfig = config.copy(fieldMappings = newFieldMappings,
        label = descriptionField.getValue
      )
      configOps.saveConfig(newConfig)
    } catch {
      case e: StorageException =>
        val message = Page.message("editConfig.error.cantSave", e.getMessage)
        showError(message)
        logger.error(message, e)
    }
  }

  private def createEditDescriptionElement(): Component = {
    val form = new FormLayout
    descriptionField.setTitle(Page.message("editConfig.description"))
    descriptionField.setWidth(Sizes.editConfigDescriptionFieldWidth)

    // can use this to auto-save field changes. don't want to do this for just one field though.
    // need to be the same experience for all fields.
//    descriptionField.addBlurListener(_ => save())
    form.add(descriptionField)
    form.setWidth(Sizes.editConfigDescriptionFormWidth)
    form
  }

  def showError(errorMessage: String): Unit = {
    errorMessageLabel.setVisible(true)
    errorMessageLabel.setText(errorMessage)
  }

  def clearErrorMessage(): Unit = {
    errorMessageLabel.setVisible(false)
    errorMessageLabel.setText("")
  }
}
