package com.taskadapter.webui.pages

import com.taskadapter.web.TaskKeeperLocationStorage
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.license.{LicenseFacade, LicensePanel}
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{BasePage, ConfigOperations, LastVersionLoader, Layout, LogFinder, SessionController, VersionComparator}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.{H2, Hr, Label}
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.{PageTitle, Route}

@Route(value = Navigator.SUPPORT, layout = classOf[Layout])
@CssImport(value = "./styles/views/mytheme.css")
class SupportPage() extends BasePage {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()
  private val storage = new TaskKeeperLocationStorage(services.rootDir)
  private val cacheFileLocation = storage.cacheFolder.getAbsolutePath()
  private val logFileLocation = LogFinder.getLogFileLocation

  private val lastVersionInfoLayout = new VerticalLayout

  buildUI()

  private def buildUI(): Unit = {
    setSpacing(true)

    addSection("supportPage.contactPanelTitle")
    addEmailPanel()

    addSection("supportPage.versionInfo")
    addVersionInfo()

    addSection("supportPage.fileLocationsPanel")
    addFileLocationsSection()

    addSection("supportPage.licenseInformation")
    createLicenseSection()
  }

  private def addVersionInfo(): Unit = {
    //    val versionPanel = new VerticalLayout(message("supportPage.versionInfo"))
    //    versionPanel.setWidth(700, Sizeable.Unit.PIXELS)
    val currentVersionLabel = new Label(message("supportPage.taskAdapterVersion", services.currentTaskAdapterVersion))
    val view = new VerticalLayout
    view.add(currentVersionLabel)
    view.setMargin(true)
    val checkButton = new Button(message("supportPage.checkForUpdate"))
    checkButton.addClickListener(_ => checkForUpdate())
    view.add(checkButton)
    view.add(lastVersionInfoLayout)
    //    versionPanel.setContent(view)
    add(view)
  }

  private def checkForUpdate(): Unit = {
    lastVersionInfoLayout.removeAll()
    try {
      val lastAvailableVersion = LastVersionLoader.loadLastVersion
      val latestVersionLabel = new Label(message("supportPage.latestAvailableVersion", lastAvailableVersion))
      lastVersionInfoLayout.add(latestVersionLabel)
      if (VersionComparator.isCurrentVersionOutdated(services.currentTaskAdapterVersion, lastAvailableVersion)) {
        lastVersionInfoLayout.add(WebAppUpdater.createDownloadLink)
      }
    } catch {
      case e: RuntimeException =>
        lastVersionInfoLayout.add(new Label(message("supportPage.cantFindInfoOnLatestVersion")))
        lastVersionInfoLayout.add(WebAppUpdater.createDownloadLink)
    }
  }

  private def createLicenseSection(): Unit = add(LicensePanel.renderLicensePanel(
    new LicenseFacade(services.licenseManager)))

  private def addSection(captionKey: String) = {
    add(new Hr(),
      new H2(message(captionKey)))
  }

  private def addFileLocationsSection(): Unit = {
    val grid = new FormLayout()
    grid.setResponsiveSteps(
      new FormLayout.ResponsiveStep("20em", 1),
      new FormLayout.ResponsiveStep("20em", 2));
    grid.add(new Label(message("supportPage.logLocation")),
      new Label(logFileLocation),
      new Label(message("supportPage.cacheFileLocation")),
      new Label(cacheFileLocation))
    add(grid)
  }

  private def addEmailPanel(): Unit = {
    val l = new VerticalLayout
    l.setMargin(true)
    val emailMessage = message("supportPage.sendUsAnEmail")
    l.add(new Label(emailMessage))
    l.add(new Label("support@taskadapter.com"))
    add(l)
  }
}