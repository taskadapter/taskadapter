package com.taskadapter.webui.results

import java.text.SimpleDateFormat

import com.taskadapter.webui.Page
import com.vaadin.data.util.BeanItem
import com.vaadin.data.util.converter.StringToBooleanConverter
import com.vaadin.ui.renderers.{DateRenderer, HtmlRenderer}
import com.vaadin.ui.{Button, Grid, VerticalLayout}

import scala.collection.JavaConverters._

class ExportResultsListPage(close: Runnable, results: Seq[ExportResultFormat],
                            showResult: (ExportResultFormat) => Unit) {

  val dateFormat = "yyyy-MM-dd HH:mm"

  val ui = new VerticalLayout()

  import com.vaadin.data.util.BeanItemContainer

  val ds = new BeanItemContainer[ExportResultFormat](classOf[ExportResultFormat], results.asJava)

  val grid = new Grid(ds)
  grid.setWidth("980px")
  grid.removeAllColumns()

  grid.addColumn("configLabel")
    .setHeaderCaption(Page.message("exportResults.column.configName"))
    .setExpandRatio(1)

  grid.addColumn("dateStarted")
    .setHeaderCaption(Page.message("exportResults.column.startedOn"))
    .setRenderer(new DateRenderer(new SimpleDateFormat(dateFormat)))
    .setExpandRatio(1)

  grid.addColumn("success")
    .setHeaderCaption(Page.message("exportResults.column.status"))
    .setConverter(
      new StringToBooleanConverter(
        Page.message("exportResults.column.status.success"),
        "<font color= 'red'>" + Page.message("exportResults.column.status.errors") + "</font>"
      )
    )
    .setRenderer(new HtmlRenderer())
    .setExpandRatio(1)

/*
  grid.addColumn("from")
    .setHeaderCaption(Page.message("exportResults.column.from"))
    .setExpandRatio(2)
*/

  grid.addColumn("to")
    .setHeaderCaption(Page.message("exportResults.column.to"))
    .setExpandRatio(2)

  grid.addColumn("createdTasksNumber")
    .setHeaderCaption(Page.message("exportResults.column.tasksCreated"))
    .setExpandRatio(1)

  grid.addColumn("updatedTasksNumber")
    .setHeaderCaption(Page.message("exportResults.column.tasksUpdated"))
    .setExpandRatio(1)

  grid.addSelectionListener { _ =>
    val result = grid.getContainerDataSource.getItem(grid.getSelectedRow).asInstanceOf[BeanItem[ExportResultFormat]].getBean
    showResult(result)
  }

  ui.addComponent(grid)

  val closeButton = new Button(Page.message("button.close"))
  closeButton.addClickListener(_ => close.run())
  ui.addComponent(closeButton)
}
