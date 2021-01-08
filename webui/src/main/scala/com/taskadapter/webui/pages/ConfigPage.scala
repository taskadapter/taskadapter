package com.taskadapter.webui.pages

import com.taskadapter.Constants
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.definition.exception.FieldNotMappedException
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.license.LicenseManager
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{ConfigId, UIConnectorConfig, UISyncConfig}
import com.taskadapter.webui.`export`.ExportResultsFragment
import com.taskadapter.webui.config.EditConfigPage
import com.taskadapter.webui.results.{ExportResultFormat, ExportResultsListPage}
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{BasePage, ConfigOperations, EventTracker, Layout, SessionController}
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.BeforeEvent
import com.taskadapter.webui.{BasePage, ConfigActionsFragment, ConfigOperations, EventTracker, ImageLoader, Page, SessionController}
import com.vaadin.flow.component.{ClickEvent, Component, ComponentEventListener}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.notification.Notification
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

@Route(value = "config", layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class ConfigPage() extends BasePage with HasUrlParameter[String] {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()

  def setParameter(event: BeforeEvent, configIdStr: String) = {
    EventTracker.trackPage("config_panel");
    removeAll()

    val configId = ConfigId(SessionController.getCurrentUserName, Integer.parseInt(configIdStr))
    val maybeConfig = configOps.getConfig(configId)
    if (maybeConfig.isDefined) {
      val config = maybeConfig.get
      add(new ConfigPanel(config, configOps, services, sandbox))
    }
  }
}

/**
  * Config panel with left/right arrows, connector names, action buttons (Delete/Clone/etc).
  */
class ConfigPanel(config: UISyncConfig,
                  configOps: ConfigOperations,
                  services: Preservices,
                  sandbox: Sandbox) extends VerticalLayout {
  private val log = LoggerFactory.getLogger(classOf[ConfigPanel])

  private val configId = config.configId

  addClassName("configPanelInConfigsList")
  setSpacing(true)
  setSizeFull()

  val configTitleLine = new Label(config.label)
  add(configTitleLine)

  val tabSheet = new ConfigPanelTabbedSheet();
  add(tabSheet)

  val previousResultsPanel = new ResultsPanel()
  val fieldMappingsPanel = new FieldMappingPanel()
  val overviewPanel = new OverviewPanel(config)

  tabSheet.addTab("Overview", overviewPanel)
  tabSheet.addTab("Field mappings", fieldMappingsPanel)
  tabSheet.addTab("Results", previousResultsPanel)

  private val rightButton = createArrow("arrow_right.png", _ => sync(config))
  private val leftButton = createArrow("arrow_left.png", _ => sync(config.reverse))

  overviewPanel.reload()
  tabSheet.showTab("Overview")

  def updateConfigTitleLine(config: UISyncConfig) : Unit = {
    configTitleLine.setText(config.label)
  }

  def createArrow(imageFileName: String, listener: ComponentEventListener[ClickEvent[Button]]): Button = {
    val leftArrow = ImageLoader.getImage(imageFileName)
    val button = new Button(leftArrow)
    button.setHeight("40px")
    button.setWidth("100px")
    button.getElement.setProperty("title", Page.message("export.exportButtonTooltip"))
//    button.addClassName(BUTTON_LARGE)
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

  private def getConfigEditor(config: UISyncConfig, error: String): Component = {
    val editor = new EditConfigPage(configOps, Page.MESSAGES, error, config)
    editor.getUI
  }

  /**
    * Performs a synchronization operation from first connector to second.
    *
    * @param config base config. May be saved!
    */
  private def sync(config: UISyncConfig): Unit = {
    exportCommon(config)
  }

  class FieldMappingPanel extends ReloadableComponent {
    val layout = new VerticalLayout()
    def show() = {
      val maybeConfig = configOps.getConfig(configId)
      if (maybeConfig.isEmpty) {
        Notification.show("The config with ID " + configId.id + " is not found")
      } else {
        layout.removeAll()
        layout.add(getConfigEditor(maybeConfig.get, ""))
      }
    }
    def ui: VerticalLayout = layout
    override def reload(): Unit = show()
  }

  class ResultsPanel() extends ReloadableComponent {
    val layout = new VerticalLayout()
    layout.setWidth("920px")

    def showResultsList(configId: ConfigId) = {
      val resultsList = new ExportResultsListPage(new java.util.function.Function[ExportResultFormat, Void] {
        override def apply(result: ExportResultFormat): Void = {
          showSingleResult(result)
          null
        }
      })
      val results = services.exportResultStorage.getSaveResults(configId);
      resultsList.showResults(results.asJava);
      layout.removeAll()
      layout.add(resultsList)
    }

    def showSingleResult(result: ExportResultFormat): Unit = {
      val fragment = new ExportResultsFragment(
        services.settingsManager.isTAWorkingOnLocalMachine)
      val ui = fragment.showExportResult(result)
      layout.removeAll()
      layout.add(ui)
    }

    def ui: VerticalLayout = layout

    override def reload(): Unit = showResultsList(config.configId)
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
      val systemPanel = connectorConfig.createMiniPanel(sandbox)
      val dialog = ModalWindow.showDialog(systemPanel.getComponent)
      dialog.addDialogCloseActionListener(_ => {
        // save the fields from the component into the original bean
        systemPanel.save()

        // save the config to disk
        configSaver.run()

        dialog.close()
      })
    }

    private val configSaver = new Runnable {
      override def run(): Unit = {
        // TODO 14 check that the new config is used here
        configOps.saveConfig(config)
        updateConfigTitleLine(config)
        recreateContents(config)
      }
    }

    private def recreateContents(config: UISyncConfig): Unit = {
      horizontalLayout.removeAll()

      val leftConnectorEditListener = new Runnable {
        override def run(): Unit = showEditConnectorDialog(config.getConnector1, configSaver, sandbox)
      }
      val leftSystemButton = createConfigureConnectorButton(config.connector1, leftConnectorEditListener)
      horizontalLayout.add(leftSystemButton)

      val leftRightButtonsPanel = new VerticalLayout()
      leftRightButtonsPanel.setSpacing(true)

      leftRightButtonsPanel.add(rightButton)
      leftRightButtonsPanel.add(leftButton)

      horizontalLayout.add(leftRightButtonsPanel)

      val rightConnectorEditListener = new Runnable {
        override def run(): Unit = showEditConnectorDialog(config.getConnector2, configSaver, sandbox)
      }
      val rightSystemButton = createConfigureConnectorButton(config.connector2, rightConnectorEditListener)
      horizontalLayout.add(rightSystemButton)

      performValidation(config, configSaver)
    }

    def showInitialState() = {
      layout.removeAll()
      val buttonsLayout = new ConfigActionsFragment(configId)

      layout.add(buttonsLayout)
      layout.add(horizontalLayout)
      layout.add(validationPanelSaveToRight.ui)
      layout.add(validationPanelSaveToLeft.ui)

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
//      val window = ModalWindow.showWindow(layout.getUI)
//      val editor = getConfigEditor(loadConfig(), error, () => {
//        window.close()
        // the config may have been changed by the editor. reload it
//        val maybeConfig = configOps.getConfig(configId)
//        if (maybeConfig.isDefined) {
//          recreateContents(maybeConfig.get)
//        }
//      })
//      window.setContent(editor)
      EventTracker.trackPage("edit_config")
    }

    override def ui: VerticalLayout = layout

    override def reload(): Unit = showInitialState()
  }

  private def exportCommon(config: UISyncConfig): Unit = {
    log.info(
      s"""Starting export
    from ${config.connector1.getConnectorTypeId} (${config.connector1.getSourceLocation})
    to   ${config.connector2.getConnectorTypeId} (${config.connector2.getDestinationLocation}""")

    EventTracker.trackPage("export_confirmation")
    val maxTasks = if (services.licenseManager.isSomeValidLicenseInstalled) {
      Constants.maxTasksToLoad
    } else {
      LicenseManager.TRIAL_TASKS_NUMBER_LIMIT
    }
    log.info(s"License installed? ${services.licenseManager.isSomeValidLicenseInstalled}")
    val panel = new ExportPage(getUI.get(), services.exportResultStorage, config, maxTasks,
      services.settingsManager.isTAWorkingOnLocalMachine,
      () => overviewPanel.reload(),
      configOps)
    overviewPanel.ui.removeAll()
    overviewPanel.ui.add(panel)
    panel.startLoading()
  }

}