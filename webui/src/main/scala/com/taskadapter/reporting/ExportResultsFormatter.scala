package com.taskadapter.reporting

import com.google.common.base.Throwables
import com.taskadapter.webui.results.ExportResultFormat

object ExportResultsFormatter {
  def toNiceString(result: ExportResultFormat): String = {
    System.lineSeparator() + "Tasks created: " + result.createdTasksNumber + System.lineSeparator() +
      "Tasks updated: " + result.updatedTasksNumber + System.lineSeparator() +
      "General errors: " + result.generalErrors + System.lineSeparator() +
      formatTaskErrors(result)
  }

  private def formatTaskErrors(result: ExportResultFormat): String = {
    "Task-specific errors: " + System.lineSeparator() +
      result.taskErrors.map(e => e._1 + " - " + e._2 + " - " +
        Throwables.getStackTraceAsString(e._3)
        + System.lineSeparator())
  }
}
