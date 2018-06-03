package com.taskadapter.reporting

import com.taskadapter.webui.results.ExportResultFormat

object ExportResultsFormatter {
  def toNiceString(result: ExportResultFormat): String = {
    System.lineSeparator() + "Tasks created: " + result.createdTasksNumber + System.lineSeparator() +
      "Tasks updated: " + result.updatedTasksNumber + System.lineSeparator() +
      "General errors: " + result.generalErrors + System.lineSeparator() +
      "Task-specific errors: " + result.taskErrors
  }
}
