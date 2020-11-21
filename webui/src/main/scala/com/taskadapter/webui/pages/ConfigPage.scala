package com.taskadapter.webui.pages

import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.ConfigId
import com.taskadapter.webui.service.Preservices
import com.taskadapter.webui.{ConfigOperations, EventTracker, SessionController}
import com.vaadin.ui.VerticalLayout

class ConfigPage() extends HasUrlParameter {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices
  private val sandbox: Sandbox = SessionController.createSandbox()

  private var panel: ConfigPanel = null

  def setParameter(event: BeforeEvent, configIdStr: String) = {
    EventTracker.trackPage("config_panel");

    val configId = ConfigId(SessionController.getCurrentUserName, Integer.parseInt(configIdStr))
    val maybeConfig = configOps.getConfig(configId)
    if (maybeConfig.isDefined) {
      val config = maybeConfig.get
      panel = new ConfigPanel(config, configOps, services, sandbox)
    }
  }

  def ui: VerticalLayout = panel.ui()
}
