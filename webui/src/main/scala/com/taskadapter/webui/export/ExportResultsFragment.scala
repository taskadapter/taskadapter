package com.taskadapter.webui.export

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.web.configeditor.file.FileDownloadResource
import com.taskadapter.web.ui.HtmlLabel
import com.taskadapter.webui.Page.message
import com.taskadapter.webui.results.ExportResultFormat
import com.taskadapter.webui.{Page, TALog}
import com.vaadin.flow.component.{Component, HasComponents, Html}
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.{H2, Label}
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}

class ExportResultsFragment(showFilePath: Boolean) {
  val log = TALog.log

  private def quot(str: String) = str.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")

  /**
    * Shows task export result.
    */
  def showExportResult(result: ExportResultFormat): Component = {
    val donePanel = new VerticalLayout()
    donePanel.setWidth("900px")
    donePanel.setClassName("export-panel")
    // TODO format inside MESSAGES formatter, not here.
    val time = new SimpleDateFormat("MMMM dd, yyyy  HH:mm").format(Calendar.getInstance.getTime)
    val label = new HtmlLabel(Page.message("export.exportCompletedOn", time))
    donePanel.add(label)

    donePanel.add(createdExportResultLabel(message("export.from"), result.from))
    donePanel.add(createdExportResultLabel(message("export.to"), result.to))

    if (result.targetFileName.isDefined && !(result.targetFileName.get == "")) {
      if (showFilePath) {
        val flabel = new HtmlLabel(Page.message("export.pathToExportFile", result.targetFileName.get))
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
  def addErrors(container: HasComponents, generalErrors: Seq[String], taskErrors: Seq[(TaskId, String, String)]): Unit = {
    if (generalErrors.isEmpty && taskErrors.isEmpty) return

    val label = new H2(Page.message("exportResults.thereWereErrors"))
    container.add(label)

    if (generalErrors.nonEmpty) {
      var generalErrorText = ""
      generalErrors.foreach { error =>
        generalErrorText += quot(error) + "<br/>\n"
      }
      val generalErrorLabel = new HtmlLabel(generalErrorText)
      generalErrorLabel.addClassName("failure")
      container.add(generalErrorLabel)
    }

    if (taskErrors.nonEmpty) {
      val taskErrorsLabel = new H2(Page.message("exportResults.taskErrors"))
      container.add(taskErrorsLabel)

      taskErrors.foreach { error =>
        val taskSourceId = error._1
        val errorText = s"Task source id $taskSourceId<br/>" + error._2
        val errorTextLabel = new HtmlLabel(errorText)
        errorTextLabel.addClassName("failure")
        container.add(errorTextLabel)
      }
    }
  }

  def createdExportResultLabel(labelName: String, labelValue: String): HorizontalLayout = {
    val lName = new HtmlLabel("<strong>" + labelName + "</strong>")
    lName.setWidth("120px")
    val lValue = new HtmlLabel("<em>" + labelValue + "</em>")
    val layout = new HorizontalLayout
    layout.add(lName)
    layout.add(lValue)
    layout.addClassName("export-result")
    layout
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
//    val resource = new FileDownloadResource(file)
//    val downloader = new FileDownloader(resource)
//    downloader.extend(downloadButton)
    downloadButton
  }

}
