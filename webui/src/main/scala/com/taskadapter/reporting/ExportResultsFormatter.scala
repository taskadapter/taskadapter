package com.taskadapter.reporting

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.webui.results.ExportResultFormat

import scala.collection.mutable.ListBuffer

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

    var results = new ListBuffer[(TaskId, String, String)]()
    results += errors.head

    for (i <- 1 until errors.size) {
      val prev = errors(i - 1)
      val current = errors(i)

      val newItem = if (current._3 == prev._3) {
        (current._1, current._2, "same as previous")
      } else {
        current
      }
      results += newItem
    }
    results.map(e => e._1 + " - " + e._2 + " - " + e._3)
      .mkString(System.lineSeparator())
  }
}
