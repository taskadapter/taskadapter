package com.taskadapter.webui.results

import java.util.Date

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.web.uiapi.ConfigId

case class ExportResultFormat(configId: ConfigId,
                              configLabel: String,
                              from: String,
                              to: String,
                              targetFileName: Option[String],
                              updatedTasksNumber: Int, createdTasksNumber: Int,
                              generalErrors: Seq[String],
                              taskErrors: Seq[(TaskId, String)],
                              dateStarted: Date,
                              timeTookSeconds: Int) {

  def hasErrors: Boolean = generalErrors.nonEmpty || taskErrors.nonEmpty
}
