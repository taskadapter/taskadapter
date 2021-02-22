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
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.data.binder.Binder
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

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
  descriptionField.setWidth(Sizes.editConfigDescriptionFieldWidth)

  var errorMessageLabel = new HtmlLabel("")
  errorMessageLabel.addClassName("error-message-label")
  errorMessageLabel.setWidth("100%")
  errorMessageLabel.setVisible(false)

  if (!Strings.isNullOrEmpty(error)) {
    showError(error)
  }
  val buttons = createConfigOperationsButtons
  buttons.setWidth("20%")
  val topRowLayout = new HorizontalLayout(descriptionField, buttons)

  val layout = new VerticalLayout(topRowLayout, errorMessageLabel)

  val taskFieldsMappingFragment = new TaskFieldsMappingFragment(messages,
    config.getConnector1.getAllFields, config.getConnector1.fieldNames, config.getConnector1.getLabel,
    config.getConnector2.getAllFields, config.getConnector2.fieldNames, config.getConnector2.getLabel,
    config.getNewMappings.asScala)

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
    val saveButton = new Button(Page.message("button.save"),
      _ => saveClicked())
    rightLayout.add(saveButton)
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
    try {
      // need to save the UI elements into the model  first
      saveUiElementsIntoModel()

      // TODO validate left/right editors too. this was lost during the last refactoring.
      taskFieldsMappingFragment.validate()
    } catch {
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

  private def saveUiElementsIntoModel(): Unit = {
    taskFieldsMappingFragment.save()
  }

  private def save(): Unit = {
    try {
      saveUiElementsIntoModel()
      val newFieldMappings = getElements.toSeq.asJava
      val newConfig = new UISyncConfig(
        config.getTaskKeeperLocationStorage,
        config.getConfigId,
        descriptionField.getValue,
        config.getConnector1,
        config.getConnector2,
        newFieldMappings,
        config.isReversed
      )
      configOps.saveConfig(newConfig)
    } catch {
      case e: StorageException =>
        val message = Page.message("editConfig.error.cantSave", e.getMessage)
        showError(message)
        logger.error(message, e)
    }
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
