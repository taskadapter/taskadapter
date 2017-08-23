package com.taskadapter.webui.export

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

import com.google.common.base.Strings
import com.taskadapter.connector.definition.{SaveResult, TaskError}
import com.taskadapter.web.configeditor.file.FileDownloadResource
import com.taskadapter.web.uiapi.UIConnectorConfig
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.{Page, TALog, Tracker}
import com.vaadin.server.FileDownloader
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme

import scala.collection.immutable.List

class ExportResultsFragment(tracker: Tracker, onDone: Runnable, showFilePath: Boolean) {
  val log = TALog.log

  private def quot(str: String) = str.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")

  /**
    * Shows task export result.
    *
    * @param res
    * operation result.
    */
  def showExportResult(sourceConfig: UIConnectorConfig, targetConfig: UIConnectorConfig, res: SaveResult): Component = {
    val donePanel = new VerticalLayout()
    donePanel.setWidth("900px")
    donePanel.setStyleName("export-panel")
    // TODO format inside MESSAGES formatter, not here.
    val time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm").format(Calendar.getInstance.getTime)
    val label = new Label(Page.message("export.exportCompletedOn", time))
    label.setContentMode(ContentMode.HTML)
    donePanel.addComponent(label)
    val sourceLocation = sourceConfig.getSourceLocation
    val targetLocation = targetConfig.getSourceLocation

    donePanel.addComponent(createdExportResultLabel(message("export.from"), sourceLocation))
    donePanel.addComponent(createdExportResultLabel(message("export.to"), targetLocation))

    val resultFile = res.getTargetFileAbsolutePath
    if (resultFile != null && !showFilePath) donePanel.addComponent(createDownloadButton(resultFile))
    donePanel.addComponent(createdExportResultLabel(message("export.createdTasks"), String.valueOf(res.getCreatedTasksNumber)))
    donePanel.addComponent(createdExportResultLabel(message("export.updatedTasks"), String.valueOf(res.getUpdatedTasksNumber) + "<br/><br/>"))

    if (!Strings.isNullOrEmpty(resultFile) && showFilePath) {
      val flabel = new Label(Page.message("export.pathToExportFile", resultFile))
      flabel.setContentMode(ContentMode.HTML)
      donePanel.addComponent(flabel)
    }

    addErrors(donePanel, targetConfig, res.getGeneralErrors, res.getTaskErrors)

    val button = new Button(message("action.acknowledge"))
    button.addClickListener(_ => onDone.run())

    val ui = new VerticalLayout
    ui.addComponent(donePanel)
    ui.addComponent(button)

    val labelForTracking = sourceConfig.getConnectorTypeId + " - " + targetConfig.getConnectorTypeId
    tracker.trackEvent("export", "finished_saving_tasks", labelForTracking)

    logErrors(res)
    ui
  }

  def logErrors(result: SaveResult): Unit = {
    log.info("Export completed. " + System.lineSeparator()
      + "Tasks created: " + result.getCreatedTasksNumber + System.lineSeparator()
      + "Tasks updated: " + result.getUpdatedTasksNumber + System.lineSeparator()
      + "General errors: " + result.getGeneralErrors + System.lineSeparator()
      + "Task-specific errors: " + result.getTaskErrors)
  }

  /**
    * Adds connector errors into the output.
    *
    * @param container     * error container.
    * @param connector     * connector, which created the errors.
    * @param generalErrors * list of general errors.
    * @param taskErrors    * errors specific for each task.
    */
  def addErrors(container: ComponentContainer, connector: UIConnectorConfig, generalErrors: List[Throwable],
                taskErrors: List[TaskError[Throwable]]): Unit = {
    if (generalErrors.isEmpty && taskErrors.isEmpty) return

    val label = new Label(Page.message("exportResults.thereWereErrors"))
    label.addStyleName(ValoTheme.LABEL_H2)
    container.addComponent(label)

    if (generalErrors.nonEmpty) {
      var generalErrorText = ""
      generalErrors.foreach { error =>
        generalErrorText += quot(connector.decodeException(error)) + "<br/>\n"
      }
      val generalErrorLabel = new Label(generalErrorText)
      generalErrorLabel.setContentMode(ContentMode.HTML)
      generalErrorLabel.addStyleName(ValoTheme.LABEL_FAILURE)
      container.addComponent(generalErrorLabel)
    }

    if (taskErrors.nonEmpty) {
      val taskErrorsLabel = new Label(Page.message("exportResults.taskErrors"))
      taskErrorsLabel.addStyleName(ValoTheme.LABEL_H2)
      container.addComponent(taskErrorsLabel)

      taskErrors.foreach { error =>
        val task = error.getTask
        val errorText = s"Task source id ${task.getSourceSystemId}<br/>" +
          connector.decodeException(error.getErrors)
        val errorTextLabel = new Label(errorText)
        errorTextLabel.addStyleName(ValoTheme.LABEL_FAILURE)
        errorTextLabel.setContentMode(ContentMode.HTML)
        container.addComponent(errorTextLabel)
      }
    }
  }

  def createdExportResultLabel(labelName: String, labelValue: String): HorizontalLayout = {
    val lName = new Label("<strong>" + labelName + "</strong>")
    lName.setContentMode(ContentMode.HTML)
    lName.setWidth("120px")
    val lValue = new Label("<em>" + labelValue + "</em>")
    lValue.setContentMode(ContentMode.HTML)
    val hl = new HorizontalLayout
    hl.addComponent(lName)
    hl.addComponent(lValue)
    hl.addStyleName("export-result")
    hl
  }

  /**
    * Creates "download file" button.
    *
    * @param targetFileAbsolutePath
    * target path.
    */
  private def createDownloadButton(targetFileAbsolutePath: String) = {
    val downloadButton = new Button(message("export.downloadFile"))
    val file = new File(targetFileAbsolutePath)
    val resource = new FileDownloadResource(file)
    val downloader = new FileDownloader(resource)
    downloader.extend(downloadButton)
    downloadButton
  }

}
