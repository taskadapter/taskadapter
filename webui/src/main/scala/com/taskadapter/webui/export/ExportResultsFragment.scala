package com.taskadapter.webui.export

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.Label
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.web.configeditor.file.FileDownloadResource
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.results.ExportResultFormat
import com.taskadapter.webui.{Page, TALog}
import com.vaadin.server.FileDownloader
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme

class ExportResultsFragment(showFilePath: Boolean) {
  val log = TALog.log

  private def quot(str: String) = str.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")

  /**
    * Shows task export result.
    *
    * @param result
    * operation result.
    */
  def showExportResult(result: ExportResultFormat): Component = {
    val donePanel = new VerticalLayout()
    donePanel.setWidth("900px")
    donePanel.setStyleName("export-panel")
    // TODO format inside MESSAGES formatter, not here.
    val time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm").format(Calendar.getInstance.getTime)
    val label = new Label(Page.message("export.exportCompletedOn", time))
    label.setContentMode(ContentMode.HTML)
    donePanel.add(label)

    donePanel.add(createdExportResultLabel(message("export.from"), result.from))
    donePanel.add(createdExportResultLabel(message("export.to"), result.to))

    if (result.targetFileName.isDefined && !(result.targetFileName.get == "")) {
      if (showFilePath) {
        val flabel = new Label(Page.message("export.pathToExportFile", result.targetFileName.get))
        flabel.setContentMode(ContentMode.HTML)
        donePanel.add(flabel)
      }
      donePanel.add(createDownloadButton(result.targetFileName.get))
    }
    donePanel.add(createdExportResultLabel(message("export.createdTasks"), String.valueOf(result.createdTasksNumber)))
    donePanel.add(createdExportResultLabel(message("export.updatedTasks"), String.valueOf(result.updatedTasksNumber) + "<br/><br/>"))


    addErrors(donePanel, result.generalErrors, result.taskErrors)

    val ui = new VerticalLayout
    ui.add(donePanel)

    ui
  }

  /**
    * Adds connector errors into the output.
    *
    * @param container     * error container.
    * @param generalErrors * list of general errors.
    * @param taskErrors    * errors for each task.
    */
  def addErrors(container: VerticalLayout, generalErrors: Seq[String], taskErrors: Seq[(TaskId, String, String)]): Unit = {
    if (generalErrors.isEmpty && taskErrors.isEmpty) return

    val label = new Label(Page.message("exportResults.thereWereErrors"))
    label.addClassName(ValoTheme.LABEL_H2)
    container.add(label)

    if (generalErrors.nonEmpty) {
      var generalErrorText = ""
      generalErrors.foreach { error =>
        generalErrorText += quot(error) + "<br/>\n"
      }
      val generalErrorLabel = new Label(generalErrorText)
      generalErrorLabel.setContentMode(ContentMode.HTML)
      generalErrorLabel.addClassName(ValoTheme.LABEL_FAILURE)
      container.add(generalErrorLabel)
    }

    if (taskErrors.nonEmpty) {
      val taskErrorsLabel = new Label(Page.message("exportResults.taskErrors"))
      taskErrorsLabel.addClassName(ValoTheme.LABEL_H2)
      container.add(taskErrorsLabel)

      taskErrors.foreach { error =>
        val taskSourceId = error._1
        val errorText = s"Task source id $taskSourceId<br/>" + error._2
        val errorTextLabel = new Label(errorText)
        errorTextLabel.addClassName(ValoTheme.LABEL_FAILURE)
        errorTextLabel.setContentMode(ContentMode.HTML)
        container.add(errorTextLabel)
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
    hl.add(lName)
    hl.add(lValue)
    hl.addClassName("export-result")
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
