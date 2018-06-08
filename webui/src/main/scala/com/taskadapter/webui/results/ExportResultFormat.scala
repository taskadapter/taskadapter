package com.taskadapter.webui.results

import java.util.Date

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.web.uiapi.ConfigId

import scala.beans.BeanProperty

/**
  * `BeanProperty` annotation is required for Vaadin to show these elements in [[ExportResultsListPage]]
  */
case class ExportResultFormat(configId: ConfigId,
                              @BeanProperty configLabel: String,
                              @BeanProperty from: String,
                              @BeanProperty to: String,
                              @BeanProperty targetFileName: Option[String],
                              @BeanProperty updatedTasksNumber: Int,
                              @BeanProperty createdTasksNumber: Int,
                              @BeanProperty generalErrors: Seq[String],
                              @BeanProperty taskErrors: Seq[(TaskId, String, String)],
                              @BeanProperty dateStarted: Date,
                              @BeanProperty timeTookSeconds: Int) {

  def hasErrors: Boolean = generalErrors.nonEmpty || taskErrors.nonEmpty
  def isSuccess: Boolean = !hasErrors
}
