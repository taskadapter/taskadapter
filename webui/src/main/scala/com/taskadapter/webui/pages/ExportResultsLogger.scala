package com.taskadapter.webui.pages

import com.taskadapter.reporting.ExportResultsFormatter
import com.taskadapter.webui.TALog
import com.taskadapter.webui.results.ExportResultFormat

object ExportResultsLogger {
  val log = TALog.log

  def log(result: ExportResultFormat, prefix: String = "Export completed."): Unit = {
    log.info(prefix + ExportResultsFormatter.toNiceString(result))
  }
}
