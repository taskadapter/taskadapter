package com.taskadapter.reporting

import com.taskadapter.web.uiapi.DecodedTaskError
import com.taskadapter.webui.results.ExportResultFormat

import scala.collection.mutable.ListBuffer

object ExportResultsFormatter {
  def toNiceString(result: ExportResultFormat): String = {
    System.lineSeparator() + "Tasks created: " + result.createdTasksNumber + System.lineSeparator() +
      "Tasks updated: " + result.updatedTasksNumber + System.lineSeparator() +
      "General errors: " + result.generalErrors + System.lineSeparator() +
      "Task-specific errors: " + System.lineSeparator() + formatTaskErrors(result.taskErrors)
  }

  def formatTaskErrors(errors: Seq[DecodedTaskError]): String = {
    if (errors.isEmpty) {
      return ""
    }

    var results = new ListBuffer[DecodedTaskError]()
    results += errors.head

    for (i <- 1 until errors.size) {
      val prev = errors(i - 1)
      val current = errors(i)

      val newItem = if (current.exceptionStackTrace == prev.exceptionStackTrace) {
        new DecodedTaskError(current.sourceSystemTaskId, current.connector2ErrorText, "same as previous")
      } else {
        new DecodedTaskError(current.sourceSystemTaskId, current.connector2ErrorText, StacktraceCleaner.stripInternalStacktraceItems(current.exceptionStackTrace))
      }
      results += newItem
    }
    results.map(e => e.sourceSystemTaskId + " - " + e.connector2ErrorText + " - " + e.exceptionStackTrace)
      .mkString(System.lineSeparator())
  }
}
