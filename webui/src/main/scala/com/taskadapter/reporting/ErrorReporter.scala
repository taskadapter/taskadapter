package com.taskadapter.reporting

import com.google.common.base.Throwables
import com.rollbar.notifier.Rollbar
import com.rollbar.notifier.config.ConfigBuilder
import com.taskadapter.web.SettingsManager
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.results.ExportResultFormat
import com.taskadapter.webui.service.CurrentVersionLoader

object ErrorReporter {
  private val appVersion = new CurrentVersionLoader().getCurrentVersion
  private val rollbar = Rollbar.init(ConfigBuilder.withAccessToken("7443b08768344185beae9cfe6828dc81").build)

  def reportIfAllowed(config: UISyncConfig, throwable: Throwable): Unit = {
    if (isAllowedToSend) {
      val fullText = getHeader(config) + Throwables.getStackTraceAsString(throwable)
      rollbar.error(fullText)
    }
  }

  def reportIfAllowed(throwable: Throwable): Unit = {
    if (isAllowedToSend) {
      rollbar.error(throwable)
    }
  }

  def reportIfAllowed(config: UISyncConfig, results: ExportResultFormat): Unit = {
    if (isAllowedToSend && results.hasErrors) {
      val mailBody = getHeader(config) + ExportResultsFormatter.toNiceString(results)
      rollbar.log(mailBody)
    }
  }

  private def getHeader(config: UISyncConfig): String = {
    getConfigInfo(config) + s"$div TaskAdapter v. $appVersion $div"
  }

  private def getConfigInfo(config: UISyncConfig): String = {
    config.getConnector1.getConnectorTypeId + " - " + config.getConnector2.getConnectorTypeId +
      div + FieldMappingFormatter.format(config.fieldMappings)
  }

  def isAllowedToSend: Boolean = new SettingsManager().isErrorReportingEnabled

  private val div = System.lineSeparator() + System.lineSeparator()
}
