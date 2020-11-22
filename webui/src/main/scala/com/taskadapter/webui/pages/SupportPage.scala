package com.taskadapter.webui.pages

import com.taskadapter.web.TaskKeeperLocationStorage
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.license.{LicenseFacade, LicensePanel}
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{BasePage, ConfigOperations, EventTracker, LastVersionLoader, LogFinder, SessionController, VersionComparator}
import com.vaadin.server.Sizeable
import com.vaadin.ui.{Button, GridLayout, Label, Panel, VerticalLayout}

class SupportPage extends BasePage {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()
  private val storage = new TaskKeeperLocationStorage(services.rootDir);

  private val lastVersionInfoLayout = new VerticalLayout
  private val cacheFileLocation =  storage.cacheFolder.getAbsolutePath()
  private val logFileLocation = LogFinder.getLogFileLocation

  buildUI()
  EventTracker.trackPage("support");

  private def buildUI(): Unit = {
    setSpacing(true)
    addEmailPanel()
    addVersionInfo()
    addFileLocationsSection()
    createLicenseSection()
  }

  private def addVersionInfo(): Unit = {
    val versionPanel = new Panel(message("supportPage.versionInfo"))
    versionPanel.setWidth(700, Sizeable.Unit.PIXELS)
    val currentVersionLabel = new Label(message("supportPage.taskAdapterVersion", services.currentTaskAdapterVersion))
    val view = new VerticalLayout
    view.addComponent(currentVersionLabel)
    view.setMargin(true)
    val checkButton = new Button(message("supportPage.checkForUpdate"))
    checkButton.addClickListener(_ => checkForUpdate())
    view.addComponent(checkButton)
    view.addComponent(lastVersionInfoLayout)
    versionPanel.setContent(view)
    addComponent(versionPanel)
  }

  private def checkForUpdate(): Unit = {
    lastVersionInfoLayout.removeAllComponents()
    try {
      val lastAvailableVersion = LastVersionLoader.loadLastVersion
      val latestVersionLabel = new Label(message("supportPage.latestAvailableVersion", lastAvailableVersion))
      lastVersionInfoLayout.addComponent(latestVersionLabel)
      if (VersionComparator.isCurrentVersionOutdated(services.currentTaskAdapterVersion, lastAvailableVersion)) lastVersionInfoLayout.addComponent(WebAppUpdater.addDownloadLink)
    } catch {
      case e: RuntimeException =>
        lastVersionInfoLayout.addComponent(new Label(message("supportPage.cantFindInfoOnLatestVersion")))
        lastVersionInfoLayout.addComponent(WebAppUpdater.addDownloadLink)
    }
  }

  private def createLicenseSection(): Unit = addComponent(LicensePanel.renderLicensePanel(
    new LicenseFacade(services.licenseManager)))

  private def addFileLocationsSection(): Unit = {
    val logsPanel = new Panel(message("supportPage.fileLocationsPanel"))
    val grid = new GridLayout(2, 2)
    grid.setWidth("100%")
    grid.setSpacing(true)
    grid.setMargin(true)
    grid.addComponent(new Label(message("supportPage.logLocation")))
    grid.addComponent(new Label(logFileLocation))
    grid.addComponent(new Label(message("supportPage.cacheFileLocation")))
    grid.addComponent(new Label(cacheFileLocation))
    logsPanel.setContent(grid)
    addComponent(logsPanel)
  }

  private def addEmailPanel(): Unit = {
    val l = new VerticalLayout
    l.setMargin(true)
    val emailMessage = message("supportPage.sendUsAnEmail")
    l.addComponent(new Label(emailMessage))
    l.addComponent(new Label("support@taskadapter.com"))
    val panel = new Panel(message("supportPage.contactPanelTitle"))
    panel.setContent(l)
    addComponent(panel)
  }
}