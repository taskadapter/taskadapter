package com.taskadapter.webui.results

import java.util.Date

import com.taskadapter.connector.definition.TaskError
import com.taskadapter.web.uiapi.ConfigId

case class ExportResultFormat(configId: ConfigId,
                              configLabel: String,
                              from: String,
                              to: String,
                              updatedTasksNumber: Int, createdTasksNumber: Int,
                              generalErrors: Seq[Throwable],
                              taskErrors: Seq[TaskError[Throwable]],
                              dateStarted: Date,
                              timeTookSeconds: Int) {

  def hasErrors: Boolean = generalErrors.nonEmpty || taskErrors.nonEmpty
}
