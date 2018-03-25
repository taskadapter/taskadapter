package com.taskadapter.webui.pages

import com.taskadapter.connector.definition.ConnectorSetup
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{SetupId, UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui._
import com.taskadapter.webui.config.EditConfigPage
import com.vaadin.shared.ui.label.ContentMode
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
                         onExit: Runnable,
                         showAllPreviousExportResults: Runnable,
                         showLastExportResult: Runnable,
                         tracker: Tracker,
                         webUserSession: WebUserSession) {
  private val log = LoggerFactory.getLogger(classOf[ConfigSummaryPanel])

  val layout = new VerticalLayout
  layout.addStyleName("configPanelInConfigsList")
  layout.setSpacing(true)

  val configId = config.id

  val buttonsLayout = new ConfigActionsFragment(config.id, configOps, onExit,
    showAllPreviousExportResults, showLastExportResult,
    () => showConfigEditor(""),
    tracker, webUserSession).layout
  layout.addComponent(buttonsLayout)

  val horizontalLayout = new HorizontalLayout
  horizontalLayout.setSpacing(true)
  layout.addComponent(horizontalLayout)
  recreateContents(horizontalLayout, config)

  val errorMessageLabel = new Label("")
  errorMessageLabel.addStyleName("error-message-label")
  errorMessageLabel.setWidth("600px")
  errorMessageLabel.addStyleName("wrap")
  errorMessageLabel.setContentMode(ContentMode.HTML)
  layout.addComponent(errorMessageLabel)

  private def recreateContents(layout: Layout, config: UISyncConfig): Unit = {
    layout.removeAllComponents()
    val configSaver = new Runnable {
      override def run(): Unit = configOps.saveConfig(config)
    }

    val leftSystemButton = createConfigureConnectorButton(layout, config.connector1, sandbox, configSaver)
    layout.addComponent(leftSystemButton)

    val leftRightButtonsPanel = new VerticalLayout()
    leftRightButtonsPanel.setSpacing(true)

    leftRightButtonsPanel.addComponent(createArrow("arrow_right.png", _ => sync(config)))
    leftRightButtonsPanel.addComponent(createArrow("arrow_left.png", _ => sync(config.reverse)))

    layout.addComponent(leftRightButtonsPanel)

    val rightSystemButton = createConfigureConnectorButton(layout, config.connector2, sandbox, configSaver)
    layout.addComponent(rightSystemButton)
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
    button.addStyleName(ValoTheme.BUTTON_LARGE)
    button.setIcon(iconResource)
    button.setWidth("270px")
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

  def showError(errorText: String): Unit = {
    errorMessageLabel.setValue(errorText)
  }

  /**
    * Prepares config for conversion.
    *
    * @param config config to prepare.
    * @return true iff conversion could be performed, false otherwise.
    */
  private def prepareForConversion(config: UISyncConfig): Boolean = {
    val from = config.getConnector1
    val to = config.getConnector2
    try
      from.validateForLoad()
    catch {
      case e: BadConfigException =>
        showError(from.decodeException(e))
        return false
    }
    var updated: ConnectorSetup = null
    try
      updated = to.updateForSave(sandbox)
    catch {
      case e: BadConfigException =>
        showError(to.decodeException(e))
        return false
    }
    // If setup was changed (e.g. a new file name was generated my MSP) - save it
    if (!(updated == to.getConnectorSetup)) try {
      configOps.saveSetup(updated, SetupId(updated.id.get))
      to.setConnectorSetup(updated)
    } catch {
      case e1: Exception =>
        val message = Page.message("export.troublesSavingConfig", e1.getMessage)
        log.error(message, e1)
        Notification.show(message, Notification.Type.ERROR_MESSAGE)
    }
    true
  }

  /**
    * Performs a synchronization operation from first connector to second.
    *
    * @param config base config. May be saved!
    */
  private def sync(config: UISyncConfig): Unit = {
    if (!prepareForConversion(config)) return
    callback.startExport(config)
  }

  def ui(): VerticalLayout = layout

}