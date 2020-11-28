package com.taskadapter.webui.results

import java.text.SimpleDateFormat

import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.vaadin14shim.Label
import com.taskadapter.webui.{Page, Sizes}
import com.vaadin.data.sort.SortOrder
import com.vaadin.data.util.BeanItem
import com.vaadin.data.util.converter.StringToBooleanConverter
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.renderers.{DateRenderer, HtmlRenderer}
import com.vaadin.ui.themes.ValoTheme
import com.vaadin.ui.Grid

import scala.collection.JavaConverters._

class ExportResultsListPage(showResult: (ExportResultFormat) => Unit) {

  val dateFormat = "yyyy-MM-dd HH:mm"

  val ui = new VerticalLayout()

  import com.vaadin.data.util.BeanItemContainer

  val ds = new BeanItemContainer[ExportResultFormat](classOf[ExportResultFormat])

  val grid = new Grid(ds)
  grid.setSizeFull()
  grid.removeAllColumns()

  grid.addColumn("configLabel")
    .setHeaderCaption(Page.message("exportResults.column.configName"))
    .setExpandRatio(1)

  grid.addColumn("dateStarted")
    .setHeaderCaption(Page.message("exportResults.column.startedOn"))
    .setRenderer(new DateRenderer(new SimpleDateFormat(dateFormat)))
    .setExpandRatio(1)

  grid.addColumn("to")
    .setHeaderCaption(Page.message("exportResults.column.to"))
    .setExpandRatio(2)

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

  private val label = new Label(Page.message("exportResults.intro"))
  label.addClassName(Sizes.tabIntro)
  ui.add(label)
  ui.add(grid)

  def showResults(results: Seq[ExportResultFormat]): Unit = {
    ds.removeAllItems()
    ds.addAll(results.asJava)
    grid.setSortOrder(List(new SortOrder("dateStarted", SortDirection.DESCENDING)).asJava)
  }
}
