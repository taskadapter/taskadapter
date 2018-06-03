package com.taskadapter.reporting

import java.io.{PrintWriter, StringWriter}

import com.taskadapter.web.SettingsManager
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.results.ExportResultFormat
import com.taskadapter.webui.service.CurrentVersionLoader

object ErrorReporter {
  private val version = new CurrentVersionLoader().getCurrentVersion

  def reportIfAllowed(config: UISyncConfig, throwable: Throwable): Unit = {
    if (isAllowedToSend) {
      val mailBody = getConfigInfo(config) + div + stacktraceToString(throwable)
      sendMail(mailBody)
    }
  }

  def reportIfAllowed(config: UISyncConfig, results: ExportResultFormat): Unit = {
    if (isAllowedToSend && results.hasErrors) {
      val mailBody = getConfigInfo(config) + div + ExportResultsFormatter.toNiceString(results)
      sendMail(mailBody)
    }
  }

  private def getConfigInfo(config: UISyncConfig): String = {
    config.getConnector1.getConnectorTypeId + " - " + config.getConnector2.getConnectorTypeId +
      div + config.fieldMappings.toString()
  }

  private def sendMail(mailBody: String): Unit = {
    val subject = s"TaskAdapter error reporter. $version"
    MailSender.sendFromGMail(MailSettings.fromEmail,
      MailSettings.fromPassword, MailSettings.toEmail, subject, mailBody)
  }

  private def stacktraceToString(throwable: Throwable): String = {
    val sw = new StringWriter
    val pw = new PrintWriter(sw)
    throwable.printStackTrace(pw)
    sw.toString
  }

  def isAllowedToSend: Boolean = new SettingsManager().isErrorReportingEnabled

  private val div = System.lineSeparator() + System.lineSeparator()
}
