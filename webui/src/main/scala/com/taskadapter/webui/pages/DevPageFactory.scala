package com.taskadapter.webui.pages

import com.taskadapter.connector.definition.{SaveResultBuilder, TaskId}
import com.taskadapter.connector.definition.exceptions.{EntityProcessingException, NotAuthorizedException}
import com.taskadapter.model.GTaskBuilder
import com.taskadapter.web.uiapi.UIConnectorConfig
import com.taskadapter.webui.Tracker
import com.taskadapter.webui.export.ExportResultsFragment
import com.vaadin.ui.Component

/**
  * sample code for faster development.
  */
object DevPageFactory {
  def getDevPage(tracker: Tracker, sourceConfig: UIConnectorConfig, targetConfig: UIConnectorConfig, onDone: Runnable): Component = {
    val page = new ExportResultsFragment(tracker, onDone, false)

    val task = GTaskBuilder.withRandom("Summary")
    task.setSourceSystemId(TaskId(100, "5999a73d05f558bce6b3b8a2"))
    val builder = new SaveResultBuilder()
    builder.setTargetFileAbsolutePath(null)
    builder.addGeneralError(new NotAuthorizedException("Not authorized to perform this operation"))

    builder.addTaskError(task, new EntityProcessingException("Due date must be greater than start date"))
    builder.addTaskError(task, new EntityProcessingException("Another error"))

    page.showExportResult(sourceConfig, targetConfig, builder.getResult)
  }
}
