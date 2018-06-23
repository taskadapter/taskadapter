package com.taskadapter.reporting

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.webui.results.ExportResultFormat

object ExportResultsFormatter {
  def toNiceString(result: ExportResultFormat): String = {
    System.lineSeparator() + "Tasks created: " + result.createdTasksNumber + System.lineSeparator() +
      "Tasks updated: " + result.updatedTasksNumber + System.lineSeparator() +
      "General errors: " + result.generalErrors + System.lineSeparator() +
      "Task-specific errors: " + System.lineSeparator() + formatTaskErrors(result.taskErrors)
  }

  def formatTaskErrors(errors: Seq[(TaskId, String, String)]): String = {
    if (errors.isEmpty) {
      return ""
    }
    errors.drop(1)
      .scanLeft(errors.head) {
        case (prev, current) => if (prev._3 == current._3) {
          (current._1, current._2, "same as previous")
        } else {
          (current._1, current._2, current._3)
        }
      }
      .map(e => e._1 + " - " + e._2 + " - " + e._3)
      .mkString(System.lineSeparator())
  }
}
