package com.taskadapter.reporting

import com.google.common.base.Throwables
import com.taskadapter.web.SettingsManager
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.results.ExportResultFormat
import com.taskadapter.webui.service.CurrentVersionLoader

object ErrorReporter {
  private val version = new CurrentVersionLoader().getCurrentVersion
  val defaultSubject = s"TaskAdapter error. $version"

  def reportIfAllowed(config: UISyncConfig, throwable: Throwable): Unit = {
    if (isAllowedToSend) {
      val mailBody = getConfigInfo(config) + div + Throwables.getStackTraceAsString(throwable)
      sendMail(defaultSubject, mailBody)
    }
  }

  def reportIfAllowed(throwable: Throwable): Unit = {
    if (isAllowedToSend) {
      val mailBody = Throwables.getStackTraceAsString(throwable)
      sendMail(defaultSubject + " - uncaught exception", mailBody)
    }
  }

  def reportIfAllowed(config: UISyncConfig, results: ExportResultFormat): Unit = {
    if (isAllowedToSend && results.hasErrors) {
      val mailBody = getConfigInfo(config) + div + ExportResultsFormatter.toNiceString(results)
      val subject = s"TaskAdapter export error. $version. Config '${config.label}'"
      sendMail(subject, mailBody)
    }
  }

  private def getConfigInfo(config: UISyncConfig): String = {
    config.getConnector1.getConnectorTypeId + " - " + config.getConnector2.getConnectorTypeId +
      div + FieldMappingFormatter.format(config.fieldMappings)
  }

  private def sendMail(subject: String, mailBody: String): Unit = {
    MailSender.sendFromGMail(MailSettings.fromEmail,
      MailSettings.fromPassword, MailSettings.toEmail, subject, mailBody)
  }

  def isAllowedToSend: Boolean = new SettingsManager().isErrorReportingEnabled

  private val div = System.lineSeparator() + System.lineSeparator()
}
