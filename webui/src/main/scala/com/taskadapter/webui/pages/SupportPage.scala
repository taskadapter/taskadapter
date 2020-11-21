package com.taskadapter.webui.pages

import com.taskadapter.web.TaskKeeperLocationStorage
import com.taskadapter.web.service.Sandbox
import com.taskadapter.webui.license.LicenseFacade
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{ConfigOperations, LogFinder, SessionController}
import com.vaadin.ui.VerticalLayout

class SupportPage {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()
  private val storage = new TaskKeeperLocationStorage(services.rootDir);

  def ui: VerticalLayout = new SupportPanel(services.currentTaskAdapterVersion,
    new LicenseFacade(services.licenseManager),
    storage.cacheFolder.getAbsolutePath(),
    LogFinder.getLogFileLocation).layout
}
