package com.taskadapter.reporting

import java.io.{PrintWriter, StringWriter}

import com.taskadapter.web.SettingsManager
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.service.CurrentVersionLoader

object ErrorReporter {
  def reportIfAllowed(config: UISyncConfig, throwable: Throwable): Unit = {
    if (isAllowedToSend) {
      report(config, throwable)
    }
  }

  private def report(config: UISyncConfig, throwable: Throwable): Unit = {
    val sw = new StringWriter
    val pw = new PrintWriter(sw)
    throwable.printStackTrace(pw)
    val exceptionTrace = sw.toString

    val mailBody = config.getConnector1.getConnectorTypeId + " - " + config.getConnector2.getConnectorTypeId +
      div +
      config.fieldMappings.toString() + div + exceptionTrace

    val version = new CurrentVersionLoader().getCurrentVersion
    val subject = s"Exception in TaskAdapter $version"
    MailSender.sendFromGMail(MailSettings.fromEmail,
      MailSettings.fromPassword, MailSettings.toEmail, subject, mailBody)
  }

  def isAllowedToSend: Boolean = new SettingsManager().isErrorReportingEnabled

  private val div = System.lineSeparator() + System.lineSeparator()
}
