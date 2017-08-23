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
    donePanel.setWidth("800px")
    donePanel.setStyleName("export-panel")
    // TODO format inside MESSAGES formatter, not here.
    val time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm").format(Calendar.getInstance.getTime)
    val label = new Label(Page.message("export.exportCompletedOn", time))
    label.setContentMode(ContentMode.HTML)
    donePanel.addComponent(label)
    val sourceLocation =sourceConfig.getSourceLocation
    val targetLocation = targetConfig.getSourceLocation

    donePanel.addComponent(createdExportResultLabel(message("export.from"), sourceLocation))
    donePanel.addComponent(createdExportResultLabel(message("export.to"), targetLocation))
    log.info("Export completed. Tasks created: " + res.getCreatedTasksNumber + ". Task updated: " + res.getUpdatedTasksNumber + " General errors: " + res.getGeneralErrors + " Task-specific errors: " + res.getTaskErrors)

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

//    VaadinSession.getCurrent.lock()
//    try
//      setContent(ui)
//    finally VaadinSession.getCurrent.unlock()
    ui
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
    container.addComponent(new Label("There were some problems during export:"))
    var errorText = ""
    val generalErrorsIter = generalErrors.iterator
    while ( {
      generalErrorsIter.hasNext
    }) {
      val t = generalErrorsIter.next
      errorText += quot(connector.decodeException(t)) + "<br/>\n"
    }
    val taskErrorsIter = taskErrors.iterator
    while ( {
      taskErrorsIter.hasNext
    }) {
      val error = taskErrorsIter.next
      errorText += "Task " + error.getTask.getId + " (\"" + error.getTask + "\"): <br/>\n" +
        connector.decodeException(error.getErrors) + "<br/>\n<br/>\n"
    }
    val errorTextLabel = new Label(errorText)
    errorTextLabel.addStyleName("errorMessage")
    errorTextLabel.setContentMode(ContentMode.HTML)
    container.addComponent(errorTextLabel)
  }

  def createdExportResultLabel(labelName: String, labelValue: String): HorizontalLayout = {
    val lName = new Label("<strong>" + labelName + "</strong>")
    lName.setContentMode(ContentMode.HTML)
    lName.setWidth("98px")
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
