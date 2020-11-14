package com.taskadapter.webui.pages

import com.taskadapter.Constants
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exception.FieldNotMappedException
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.license.LicenseManager
import com.taskadapter.web.event.{ConfigSaveRequested, EventBusImpl, StartExportRequested}
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{ConfigId, UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui._
import com.taskadapter.webui.`export`.ExportResultsFragment
import com.taskadapter.webui.config.EditConfigPage
import com.taskadapter.webui.results.{ExportResultFormat, ExportResultsListPage}
import com.taskadapter.webui.service.Preservices
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.slf4j.LoggerFactory

/**
  * Config panel with left/right arrows, connector names, action buttons (Delete/Clone/etc).
  */
class ConfigPanel(config: UISyncConfig,
                  configOps: ConfigOperations,
                  services: Preservices,
                  sandbox: Sandbox,
                  tracker: Tracker) {
  private val log = LoggerFactory.getLogger(classOf[ConfigPanel])

  private val configId = config.id

  val layout = new VerticalLayout
  layout.addStyleName("configPanelInConfigsList")
  layout.setSpacing(true)
  layout.setSizeFull()

  val configTitleLine = new Label(config.label)
  layout.addComponent(configTitleLine)

  EventBusImpl.subscribe((e: ConfigSaveRequested) => {
    configTitleLine.setValue(e.config.label)
  })

  val tabSheet = new ConfigPanelTabbedSheet();
  layout.addComponent(tabSheet.ui)

  val previousResultsPanel = new ResultsPanel(configId)
  val fieldMappingsPanel = createFieldMappingsPanel()
  val overviewPanel = new OverviewPanel(config)

  tabSheet.addTab("Overview", overviewPanel)
  tabSheet.addTab("Field mappings", new SimpleTab(fieldMappingsPanel))
  tabSheet.addTab("Results", previousResultsPanel)

  private val rightButton = createArrow("arrow_right.png", _ => sync(config))
  private val leftButton = createArrow("arrow_left.png", _ => sync(config.reverse))

  overviewPanel.reload()
  tabSheet.showTab("Overview")

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

  private def createConfigureConnectorButton(connectorConfig: UIConnectorConfig,
                                             buttonListener: Runnable): Component = {
    val iconResource = ImageLoader.getImage("edit.png")
    val button = new Button(connectorConfig.getLabel)
    button.setIcon(iconResource)
    button.setWidth("300px")
    button.setHeight("100%")
    button.addClickListener(_ => buttonListener.run())
    button
  }

  def loadConfig(): UISyncConfig = {
    val maybeConfig = configOps.getConfig(configId)
    if (maybeConfig.isEmpty) {
      throw new RuntimeException(s"Config with id $configId is not found")
    }
    maybeConfig.get
  }

  private def getConfigEditor(config: UISyncConfig, error: String, closeAction: Runnable): Component = {
    val editor = new EditConfigPage(Page.MESSAGES, error, config,
      () => closeAction.run())
    editor.getUI
  }

  /**
    * Performs a synchronization operation from first connector to second.
    *
    * @param config base config. May be saved!
    */
  private def sync(config: UISyncConfig): Unit = {
    EventBusImpl.post(StartExportRequested(config))
  }

  def createFieldMappingsPanel(): Component = {
    getConfigEditor(config, "", () => {})
  }

  class ResultsPanel(configId: ConfigId) extends ReloadableComponent {
    val layout = new VerticalLayout()
    layout.setWidth("920px")

    def showResultsList() = {
      val resultsList = new ExportResultsListPage(result => {
        showSingleResult(result)
      })
      val results = services.exportResultStorage.getSaveResults(configId);
      resultsList.showResults(results);
      layout.removeAllComponents()
      layout.addComponent(resultsList.ui)
    }

    def showSingleResult(result: ExportResultFormat): Unit = {
      val fragment = new ExportResultsFragment(
        services.settingsManager.isTAWorkingOnLocalMachine)
      val ui = fragment.showExportResult(result)
      layout.removeAllComponents()
      layout.addComponent(ui)
    }

    def ui: VerticalLayout = layout

    override def reload(): Unit = showResultsList()
  }

  class OverviewPanel(config: UISyncConfig) extends ReloadableComponent {
    val layout = new VerticalLayout()

    val horizontalLayout = new HorizontalLayout
    horizontalLayout.setSpacing(true)

    val validationPanelSaveToRight = new ValidationMessagesPanel(
      Page.message("configSummary.validationPanelCaption", config.getConnector2.getLabel))
    val validationPanelSaveToLeft = new ValidationMessagesPanel(
      Page.message("configSummary.validationPanelCaption", config.getConnector1.getLabel))

    private def showEditConnectorDialog(connectorConfig: UIConnectorConfig,
                                        configSaver: Runnable, sandbox: Sandbox): Unit = {
      val window = ModalWindow.showWindow(horizontalLayout.getUI)

      val systemPanel = connectorConfig.createMiniPanel(sandbox)
      window.setContent(systemPanel)
      window.addCloseListener(_ => configSaver.run())
    }

    private def recreateContents(config: UISyncConfig): Unit = {
      horizontalLayout.removeAllComponents()
      val configSaver = new Runnable {
        override def run(): Unit = {
          EventBusImpl.post(ConfigSaveRequested(config))
          recreateContents(config)
        }
      }
      val leftConnectorEditListener = new Runnable {
        override def run(): Unit = showEditConnectorDialog(config.getConnector1, configSaver, sandbox)
      }
      val leftSystemButton = createConfigureConnectorButton(config.connector1, leftConnectorEditListener)
      horizontalLayout.addComponent(leftSystemButton)

      val leftRightButtonsPanel = new VerticalLayout()
      leftRightButtonsPanel.setSpacing(true)

      leftRightButtonsPanel.addComponent(rightButton)
      leftRightButtonsPanel.addComponent(leftButton)

      horizontalLayout.addComponent(leftRightButtonsPanel)

      val rightConnectorEditListener = new Runnable {
        override def run(): Unit = showEditConnectorDialog(config.getConnector2, configSaver, sandbox)
      }
      val rightSystemButton = createConfigureConnectorButton(config.connector2, rightConnectorEditListener)
      horizontalLayout.addComponent(rightSystemButton)

      performValidation(config, configSaver)

      EventBusImpl.subscribe((e: StartExportRequested) => {
        exportCommon(e.config);
      })
    }

    def showInitialState() = {
      layout.removeAllComponents()
      val buttonsLayout = new ConfigActionsFragment(configId).layout

      layout.addComponent(buttonsLayout)
      layout.addComponent(horizontalLayout)
      layout.addComponent(validationPanelSaveToRight.ui)
      layout.addComponent(validationPanelSaveToLeft.ui)

      recreateContents(config)
    }

    def performValidation(config: UISyncConfig, configSaver: Runnable): Unit = {
      val errorsSaveToLeft = validateSaveToLeft(config, configSaver)
      leftButton.setEnabled(errorsSaveToLeft.isEmpty)
      validationPanelSaveToLeft.show(errorsSaveToLeft)

      val errorsSaveToRight = validateSaveToRight(config, configSaver)
      rightButton.setEnabled(errorsSaveToRight.isEmpty)
      validationPanelSaveToRight.show(errorsSaveToRight)
    }

    def validateSaveToLeft(config: UISyncConfig, configSaver: Runnable): Seq[ValidationErrorTextWithProcessor] = {
      val loadErrors = validateLoad(config.getConnector2, config.fieldMappings, configSaver)
      val saveErrors = validateSave(config.getConnector1, config.fieldMappings, configSaver)
      loadErrors ++ saveErrors
    }

    def validateSaveToRight(config: UISyncConfig, configSaver: Runnable): Seq[ValidationErrorTextWithProcessor] = {
      val loadErrors = validateLoad(config.getConnector1, config.fieldMappings, configSaver)
      val saveErrors = validateSave(config.getConnector2, config.fieldMappings, configSaver)
      loadErrors ++ saveErrors
    }

    def validateSave(uiConfig: UIConnectorConfig, fieldMappings: Seq[FieldMapping[_]], configSaver: Runnable): Seq[ValidationErrorTextWithProcessor] = {
      val errors = uiConfig.validateForSave(fieldMappings)
      errors.map(e => buildItem(uiConfig, e, configSaver))
    }

    def validateLoad(uiConfig: UIConnectorConfig, fieldMappings: Seq[FieldMapping[_]], configSaver: Runnable): Seq[ValidationErrorTextWithProcessor] = {
      val errors = uiConfig.validateForLoad()
      errors.map(e => buildItem(uiConfig, e, configSaver))
    }

    def buildItem(uiConfig: UIConnectorConfig, e: BadConfigException, configSaver: Runnable): ValidationErrorTextWithProcessor = {
      ValidationErrorTextWithProcessor(uiConfig.decodeException(e), buildFixProcessor(uiConfig, e, configSaver))
    }

    private def buildFixProcessor(uiConnectorConfig: UIConnectorConfig, e: BadConfigException, configSaver: Runnable): Runnable =
      () => {
        e match {
          case _: FieldNotMappedException => showConfigEditor(uiConnectorConfig.decodeException(e))
          case _ => showEditConnectorDialog(uiConnectorConfig, configSaver, sandbox)
        }
      }

    private def showConfigEditor(error: String): Unit = {
      val window = ModalWindow.showWindow(layout.getUI)
      val editor = getConfigEditor(loadConfig(), error, () => {
        window.close()
        // the config may have been changed by the editor. reload it
        val maybeConfig = configOps.getConfig(configId)
        if (maybeConfig.isDefined) {
          recreateContents(maybeConfig.get)
        }
      })
      window.setContent(editor)
      tracker.trackPage("edit_config")
    }

    override def ui: VerticalLayout = layout

    override def reload(): Unit = showInitialState()
  }

  private def exportCommon(config: UISyncConfig): Unit = {
    log.info(
      s"""Starting export
    from ${config.connector1.getConnectorTypeId} (${config.connector1.getSourceLocation})
    to   ${config.connector2.getConnectorTypeId} (${config.connector2.getDestinationLocation}""")

    tracker.trackPage("export_confirmation")
    val maxTasks = if (services.licenseManager.isSomeValidLicenseInstalled) {
      Constants.maxTasksToLoad
    } else {
      LicenseManager.TRIAL_TASKS_NUMBER_LIMIT
    }
    log.info(s"License installed? ${services.licenseManager.isSomeValidLicenseInstalled}")
    val panel = new ExportPage(services.exportResultStorage, config, maxTasks,
      services.settingsManager.isTAWorkingOnLocalMachine,
      () => overviewPanel.reload(), tracker)
    overviewPanel.ui.removeAllComponents()
    overviewPanel.ui.addComponent(panel.ui)
  }

  def ui(): VerticalLayout = layout
}