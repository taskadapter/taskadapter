package com.taskadapter.webui.pages

import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{ConfigOperations, SessionController}
import com.vaadin.ui.VerticalLayout

class ConfigPage(config: UISyncConfig) {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()

  def ui: VerticalLayout = new ConfigPanel(config, configOps, services, sandbox).layout
}
