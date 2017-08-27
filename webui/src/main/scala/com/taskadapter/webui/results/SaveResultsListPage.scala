package com.taskadapter.webui.results

import java.text.SimpleDateFormat
import java.util.Date

import com.taskadapter.webui.Page
import com.vaadin.ui.renderers.DateRenderer
import com.vaadin.ui.{Button, Grid, VerticalLayout}

class SaveResultsListPage(close: Runnable, results: Seq[ExportResultFormat]) {

  val dateFormat = "yyyy-MM-dd HH:mm"

  val ui = new VerticalLayout()
  val grid = new Grid
  grid.setWidth("980px")
  grid.addColumn("configName", classOf[String])
    .setHeaderCaption(Page.message("exportResults.column.configName"))
    .setExpandRatio(1)

  grid.addColumn("dateStarted", classOf[Date])
    .setHeaderCaption(Page.message("exportResults.column.startedOn"))
    .setRenderer(new DateRenderer(new SimpleDateFormat(dateFormat)))
    .setExpandRatio(1)

  grid.addColumn("from", classOf[String])
    .setHeaderCaption(Page.message("exportResults.column.from"))
    .setExpandRatio(2)

  grid.addColumn("to", classOf[String])
    .setHeaderCaption(Page.message("exportResults.column.to"))
    .setExpandRatio(2)

  grid.addColumn("createdTasksNumber", classOf[Integer])
    .setHeaderCaption(Page.message("exportResults.column.tasksCreated"))
    .setExpandRatio(1)

  grid.addColumn("updatedTasksNumber", classOf[Integer])
    .setHeaderCaption(Page.message("exportResults.column.tasksUpdated"))
    .setExpandRatio(1)

  grid.addColumn("status", classOf[String])
    .setHeaderCaption(Page.message("exportResults.column.status"))
    .setExpandRatio(1)

  results.foreach { r =>
    val status = if (r.hasErrors) {
      Page.message("exportResults.column.status.errors")
    } else {
      Page.message("exportResults.column.status.success")
    }
    grid.addRow(Seq(r.configLabel,
      r.dateStarted,
      r.from,
      r.to,
      r.createdTasksNumber.asInstanceOf[Object],
      r.updatedTasksNumber.asInstanceOf[Object],
      status
    ): _*)
  }

  import com.vaadin.ui.Notification

  grid.addSelectionListener(_ =>
    Notification.show("Select row: " + grid.getSelectedRow)
  )


  ui.addComponent(grid)

  val closeButton = new Button(Page.message("button.close"))
  closeButton.addClickListener(_ => close.run())
  ui.addComponent(closeButton)
}
