package com.taskadapter.webui.config

import com.taskadapter.config.StorageException
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.CloneDeletePanel
import com.taskadapter.webui.ConfigOperations
import com.taskadapter.webui.Page
import com.taskadapter.webui.data.ExceptionFormatter
import com.vaadin.data.util.MethodProperty
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.Alignment
import com.vaadin.ui.Button
import com.vaadin.ui.Component
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Notification
import com.vaadin.ui.TextField
import com.vaadin.ui.VerticalLayout
import org.slf4j.LoggerFactory
import com.vaadin.server.Sizeable.Unit.PERCENTAGE
import scala.collection.JavaConverters._

object EditConfigPage {

  trait Callback {
    /**
      * User requested synchronization in "forward" directions (from left to
      * right).
      *
      * @param config
      * config for the operation.
      */
    def forwardSync(config: UISyncConfig): Unit

    /**
      * User requested synchronization in "reverse" direction (from right to
      * left).
      *
      * @param config
      * config for the operation.
      */
    def backwardSync(config: UISyncConfig): Unit

    /**
      * User attempts to leave this page.
      */
    def back(): Unit
  }

  /**
    * Creates "edit description" element for the config.
    *
    * @param config
    * config.
    * @return description editor.
    */
  private def createEditDescriptionElement(config: UISyncConfig) = {
    val descriptionLayout = new HorizontalLayout
    descriptionLayout.setSpacing(true)
    descriptionLayout.addComponent(new Label(Page.message("editConfig.description")))
    val descriptionField = new TextField
    descriptionField.addStyleName("configEditorDescriptionLabel")
    val label = new MethodProperty[String](config, "label")
    descriptionField.setPropertyDataSource(label)
    descriptionLayout.addComponent(descriptionField)
    descriptionLayout
  }

  /**
    * Renders a new config editor.
    *
    * @param config
    * config to edit.
    * @param operations
    * config operations.
    * @param error
    * optional welcome error. May be null.
    * @param callback
    * callback to invoke when user attempts to leave this page.
    * @return edit page UI.
    */
  def render(config: UISyncConfig, operations: ConfigOperations, allowFullFSAccess: Boolean, error: String,
             callback: EditConfigPage.Callback): Component =
    new EditConfigPage(config, operations, allowFullFSAccess, error, callback).layout
}

class EditConfigPage private(val config: UISyncConfig, val configOps: ConfigOperations, val allowFullFSAccess: Boolean,
                             val error: String, val callback: EditConfigPage.Callback) {
  private val logger = LoggerFactory.getLogger(classOf[EditConfigPage])

  var layout = new VerticalLayout

  layout.setSpacing(true)
  layout.addComponent(ConfigsListLinkComponent.render(_ => callback.back()))
  val buttonsLayout = new HorizontalLayout
  buttonsLayout.setWidth(100, PERCENTAGE)
  val cloneDeletePanel: Component = CloneDeletePanel.render(config, configOps, () => callback.back)

  buttonsLayout.addComponent(cloneDeletePanel)
  buttonsLayout.setComponentAlignment(cloneDeletePanel, Alignment.MIDDLE_RIGHT)
  layout.addComponent(buttonsLayout)
  layout.addComponent(EditConfigPage.createEditDescriptionElement(config))
  var editor = new OnePageEditor(Page.MESSAGES, new Sandbox(allowFullFSAccess, configOps.syncSandbox), config, mkExportAction(new Runnable() {
    override def run(): Unit = {
      callback.backwardSync(config)
    }
  }), mkExportAction(new Runnable() {
    override def run(): Unit = {
      callback.forwardSync(config)
    }
  }))
  layout.addComponent(editor.getUI)
  var errorMessageLabel = new Label(error)
  errorMessageLabel.addStyleName("error-message-label")
  errorMessageLabel.setWidth(100, PERCENTAGE)
  errorMessageLabel.setContentMode(ContentMode.HTML)
  layout.addComponent(errorMessageLabel)
  layout.addComponent(createBottomButtons)

  private def createBottomButtons = {
    val buttonsLayout = new HorizontalLayout
    buttonsLayout.setWidth(100, PERCENTAGE)
    val rightLayout = new HorizontalLayout
    buttonsLayout.addComponent(rightLayout)
    buttonsLayout.setComponentAlignment(rightLayout, Alignment.BOTTOM_RIGHT)
    val saveButton = new Button(Page.message("button.save"))
    saveButton.addClickListener(new Button.ClickListener() {
      override def buttonClick(event: Button.ClickEvent): Unit = {
        saveClicked()
      }
    })
    rightLayout.addComponent(saveButton)
    val backButton = new Button(Page.message("button.close"))
    backButton.addClickListener(new Button.ClickListener() {
      override def buttonClick(event: Button.ClickEvent): Unit = {
        callback.back()
      }
    })
    rightLayout.addComponent(backButton)
    buttonsLayout
  }

  private def saveClicked() = {
    if (validate) {
      save()
      Notification.show("", Page.message("editConfig.messageSaved"), Notification.Type.HUMANIZED_MESSAGE)
    }
  }

  /** Wraps an export action. */
  private def mkExportAction(exporter: Runnable): Runnable = () => {
    if (validate) {
      save()
      exporter.run()
    }
  }

  private def validate: Boolean = {
    errorMessageLabel.setValue("")
    try
      editor.validate()
    catch {
      case e: FieldAlreadyMappedException =>
        val s = Page.message("editConfig.error.fieldAlreadyMapped", e.getValue)
        errorMessageLabel.setValue(s)
        return false
      case e: FieldNotMappedException =>
        val fieldDisplayName = e.toString
        // GTaskDescriptor.getDisplayValue(e.getField());
        val s = Page.message("error.fieldSelectedForExportNotMapped", fieldDisplayName)
        errorMessageLabel.setValue(s)
        return false
      case e: BadConfigException =>
        val s = ExceptionFormatter.format(e)
        errorMessageLabel.setValue(s)
        return false
    }
    true
  }

  private def save() = {
    try {
      val newFieldMappings = editor.getElements.asScala.toSeq
      val newConfig = config.copy(fieldMappings = newFieldMappings)
      configOps.saveConfig(newConfig)
    } catch {
      case e: StorageException =>
        val message = Page.message("editConfig.error.cantSave", e.getMessage)
        errorMessageLabel.setValue(message)
        logger.error(message, e)
    }
  }

  def setErrorMessage(errorMessage: String): Unit = {
    errorMessageLabel.setValue(errorMessage)
  }
}
