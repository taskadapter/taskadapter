package com.taskadapter.webui.pages

import com.taskadapter.webui.TALog
import com.taskadapter.webui.results.ExportResultFormat

object ExportResultsLogger {
  val log = TALog.log

  def log(result: ExportResultFormat): Unit = {
    log.info("Export completed. " + System.lineSeparator()
      + "Tasks created: " + result.createdTasksNumber + System.lineSeparator()
      + "Tasks updated: " + result.updatedTasksNumber + System.lineSeparator()
      + "General errors: " + result.generalErrors + System.lineSeparator()
      + "Task-specific errors: " + result.taskErrors)
  }
}
