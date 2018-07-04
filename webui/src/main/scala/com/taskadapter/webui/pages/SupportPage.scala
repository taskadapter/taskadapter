package com.taskadapter.webui.pages

import com.taskadapter.webui.Page.message
import com.taskadapter.webui.{LastVersionLoader, Tracker, VersionComparator}
import com.taskadapter.webui.license.{LicenseFacade, LicensePanel}
import com.vaadin.server.Sizeable
import com.vaadin.ui.{Button, Component, GridLayout, Label, Panel, VerticalLayout}

object SupportPage {
  def render(taVersion: String, license: LicenseFacade, tracker: Tracker, cacheFileLocation: String,
             logFileLocation: String): Component =
    new SupportPage(taVersion, license, tracker, cacheFileLocation, logFileLocation).layout
}

class SupportPage private(currentTaskAdapterVersion: String, licenseManager: LicenseFacade,
                          tracker: Tracker, cacheFileLocation: String, logFileLocation: String) {
  private val layout = new VerticalLayout
  private val lastVersionInfoLayout = new VerticalLayout

  buildUI()

  private def buildUI(): Unit = {
    layout.setSpacing(true)
    addEmailPanel()
    addVersionInfo()
    addFileLocationsSection()
    createLicenseSection()
  }

  private def addVersionInfo(): Unit = {
    val versionPanel = new Panel(message("supportPage.versionInfo"))
    versionPanel.setWidth(700, Sizeable.Unit.PIXELS)
    val currentVersionLabel = new Label(message("supportPage.taskAdapterVersion", currentTaskAdapterVersion))
    val view = new VerticalLayout
    view.addComponent(currentVersionLabel)
    view.setMargin(true)
    val checkButton = new Button(message("supportPage.checkForUpdate"))
    checkButton.addClickListener(_ => checkForUpdate())
    view.addComponent(checkButton)
    view.addComponent(lastVersionInfoLayout)
    versionPanel.setContent(view)
    layout.addComponent(versionPanel)
  }

  private def checkForUpdate(): Unit = {
    lastVersionInfoLayout.removeAllComponents()
    try {
      val lastAvailableVersion = LastVersionLoader.loadLastVersion
      val latestVersionLabel = new Label(message("supportPage.latestAvailableVersion", lastAvailableVersion))
      lastVersionInfoLayout.addComponent(latestVersionLabel)
      if (VersionComparator.isCurrentVersionOutdated(currentTaskAdapterVersion, lastAvailableVersion)) lastVersionInfoLayout.addComponent(WebAppUpdater.addDownloadLink)
    } catch {
      case e: RuntimeException =>
        lastVersionInfoLayout.addComponent(new Label(message("supportPage.cantFindInfoOnLatestVersion")))
        lastVersionInfoLayout.addComponent(WebAppUpdater.addDownloadLink)
    }
  }

  private def createLicenseSection(): Unit = layout.addComponent(LicensePanel.renderLicensePanel(licenseManager, tracker))

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
    this.layout.addComponent(logsPanel)
  }

  private def addEmailPanel(): Unit = {
    val l = new VerticalLayout
    l.setMargin(true)
    val emailMessage = message("supportPage.sendUsAnEmail")
    l.addComponent(new Label(emailMessage))
    l.addComponent(new Label("support@taskadapter.com"))
    val panel = new Panel(message("supportPage.contactPanelTitle"))
    panel.setContent(l)
    layout.addComponent(panel)
  }
}