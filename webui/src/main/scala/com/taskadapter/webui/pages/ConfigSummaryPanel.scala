package com.taskadapter.webui.pages

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{SetupId, UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui._
import com.taskadapter.webui.config.EditConfigPage
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.slf4j.LoggerFactory

/**
  * Config Summary panel with left/right arrows, connector names, action buttons (Delete/Clone/etc).
  */
class ConfigSummaryPanel(config: UISyncConfig, mode: DisplayMode, callback: ConfigsPage.Callback,
                         configOps: ConfigOperations,
                         sandbox: Sandbox,
                         onConfigChanges: Runnable,
                         showAllPreviousExportResults: Runnable,
                         showLastExportResult: Runnable,
                         tracker: Tracker,
                         webUserSession: WebUserSession) {
  private val log = LoggerFactory.getLogger(classOf[ConfigSummaryPanel])

  val layout = new VerticalLayout
  layout.addStyleName("configPanelInConfigsList")
  layout.setSpacing(true)

  val configId = config.id

  val buttonsLayout = new ConfigActionsFragment(config.id, configOps, onConfigChanges,
    showAllPreviousExportResults, showLastExportResult,
    () => showConfigEditor(""),
    tracker, webUserSession).layout
  layout.addComponent(buttonsLayout)

  val validationPanelSaveToRight = new ValidationMessagesPanel(
    Page.message("configSummary.validationPanelCaption", config.getConnector2.getLabel))
  val validationPanelSaveToLeft = new ValidationMessagesPanel(
    Page.message("configSummary.validationPanelCaption", config.getConnector1.getLabel))

  val horizontalLayout = new HorizontalLayout
  horizontalLayout.setSpacing(true)
  layout.addComponent(horizontalLayout)

  layout.addComponent(validationPanelSaveToRight.ui)
  layout.addComponent(validationPanelSaveToLeft.ui)

  private val rightButton = createArrow("arrow_right.png", _ => sync(config))
  private val leftButton = createArrow("arrow_left.png", _ => sync(config.reverse))

  recreateContents(horizontalLayout, config)

  private def recreateContents(layout: Layout, config: UISyncConfig): Unit = {
    layout.removeAllComponents()
    val configSaver = new Runnable {
      override def run(): Unit = {
        configOps.saveConfig(config)
        performValidation(config)
      }
    }

    val leftSystemButton = createConfigureConnectorButton(layout, config.connector1, sandbox, configSaver)
    layout.addComponent(leftSystemButton)

    val leftRightButtonsPanel = new VerticalLayout()
    leftRightButtonsPanel.setSpacing(true)

    leftRightButtonsPanel.addComponent(rightButton)
    leftRightButtonsPanel.addComponent(leftButton)

    layout.addComponent(leftRightButtonsPanel)

    val rightSystemButton = createConfigureConnectorButton(layout, config.connector2, sandbox, configSaver)
    layout.addComponent(rightSystemButton)

    performValidation(config)
  }

  def createArrow(imageFileName: String, listener: ClickListener): Button = {
    val leftArrow = ImageLoader.getImage(imageFileName)
    val button = new Button(leftArrow)
    button.setHeight("40px")
    button.setWidth("100px")
    button.setDescription(Page.message("export.exportButtonTooltip"))
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.addClickListener(listener)
    button
  }

  private def createConfigureConnectorButton(layout: Layout, connectorConfig: UIConnectorConfig,
                                             sandbox: Sandbox,
                                             configSaver: Runnable): Component = {
    val iconResource = ImageLoader.getImage("edit.png")
    val button = new Button(connectorConfig.getLabel)
    button.setIcon(iconResource)
    button.setWidth("300px")
    button.setHeight("100%")
    button.addClickListener(_ => showEditConnectorDialog(layout.getUI, connectorConfig, configSaver, sandbox))
    button
  }

  private def showEditConnectorDialog(ui: UI, connectorConfig: UIConnectorConfig,
                                      configSaver: Runnable, sandbox: Sandbox): Unit = {
    val window = ModalWindow.showWindow(ui)

    val systemPanel = connectorConfig.createMiniPanel(sandbox)
    window.setContent(systemPanel)
    window.addCloseListener(_ => configSaver.run())
  }

  private def showConfigEditor(error: String): Unit = {
    val window = ModalWindow.showWindow(layout.getUI)
    val editor = getConfigEditor(loadConfig(), error, () => {
      window.close()
      // the config may have been changed by the editor. reload it
      val maybeConfig = configOps.getConfig(configId)
      if (maybeConfig.isDefined) {
        recreateContents(horizontalLayout, maybeConfig.get)
        onConfigChanges.run()
      }
    })
    window.setContent(editor)
    tracker.trackPage("edit_config")
    webUserSession.setCurrentConfigId(config.id)
  }

  def loadConfig(): UISyncConfig = {
    val maybeConfig = configOps.getConfig(configId)
    if (maybeConfig.isEmpty) {
      throw new RuntimeException(s"Config with id $configId is not found")
    }
    maybeConfig.get
  }

  private def getConfigEditor(config: UISyncConfig, error: String, closeAction: Runnable): Component = {
    val editor = new EditConfigPage(Page.MESSAGES, tracker, configOps, error, sandbox, config,
      () => closeAction.run())
    editor.getUI
  }

  def performValidation(config: UISyncConfig) : Unit = {
    val errorsSaveToLeft = validateSaveToLeft(config)
    leftButton.setEnabled(errorsSaveToLeft.isEmpty)
    validationPanelSaveToLeft.show(errorsSaveToLeft)

    val errorsSaveToRight = validateSaveToRight(config)
    rightButton.setEnabled(errorsSaveToRight.isEmpty)
    validationPanelSaveToRight.show(errorsSaveToRight)
  }

  def validateSaveToLeft(config: UISyncConfig) : Seq[String] = {
    val loadErrors = validateLoad(config.getConnector2, config.fieldMappings)
    val saveErrors = validateSave(config.getConnector1, config.fieldMappings)
    loadErrors ++ saveErrors
  }

  def validateSaveToRight(config: UISyncConfig) : Seq[String] = {
    val loadErrors = validateLoad(config.getConnector1, config.fieldMappings)
    val saveErrors = validateSave(config.getConnector2, config.fieldMappings)
    loadErrors ++ saveErrors
  }

  def validateSave(uiConfig: UIConnectorConfig, fieldMappings: Seq[FieldMapping[_]]): Seq[String] = {
    try {
      val updated = uiConfig.updateForSave(sandbox, fieldMappings)
      // If setup was changed (e.g. a new file name was generated my MSP) - save it
      if (!(updated == uiConfig.getConnectorSetup)) try {
        configOps.saveSetup(updated, SetupId(updated.id.get))
        uiConfig.setConnectorSetup(updated)
      } catch {
        case e: Exception =>
          val message = Page.message("export.troublesSavingConfig", e.getMessage)
          log.error(message, e)
          Notification.show(message, Notification.Type.ERROR_MESSAGE)
      }
      Seq()
    } catch {
      case e: BadConfigException =>
        val message = uiConfig.decodeException(e)
        Seq(message)
    }
  }

  def validateLoad(uiConfig: UIConnectorConfig, fieldMappings: Seq[FieldMapping[_]]): Seq[String] = {
    val errors = uiConfig.validateForLoad()
    errors.map(e => uiConfig.decodeException(e.error))
  }

  /**
    * Performs a synchronization operation from first connector to second.
    *
    * @param config base config. May be saved!
    */
  private def sync(config: UISyncConfig): Unit = {
    callback.startExport(config)
  }

  def ui(): VerticalLayout = layout

}