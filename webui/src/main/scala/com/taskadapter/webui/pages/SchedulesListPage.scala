package com.taskadapter.webui.pages

import com.taskadapter.web.uiapi.Schedule
import com.taskadapter.webui.{Page, Sizes}
import com.vaadin.data.sort.SortOrder
import com.vaadin.data.util.BeanItem
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui._

import scala.collection.JavaConverters._

class SchedulesListPage(showResult: (Schedule) => Unit, showNewScheduleEditor: Runnable) {

  val ui = new VerticalLayout()

  import com.vaadin.data.util.BeanItemContainer

  val addButton = new Button(Page.message("schedules.newButton"))
  addButton.addClickListener(_ => showNewScheduleEditor.run())

  val ds = new BeanItemContainer[Schedule](classOf[Schedule])

  val grid = new Grid(ds)
  grid.setWidth("980px")
  grid.removeAllColumns()

  grid.addColumn("configLabel")
    .setHeaderCaption(Page.message("schedules.column.label"))
    .setExpandRatio(1)


  grid.addColumn("intervalInMinutes")
    .setHeaderCaption(Page.message("schedules.column.interval"))
    .setExpandRatio(1)

  /*
    grid.addColumn("to")
      .setHeaderCaption(Page.message("schedules.column.to"))
      .setExpandRatio(2)

  */
  grid.addSelectionListener { _ =>
    val result = grid.getContainerDataSource.getItem(grid.getSelectedRow).asInstanceOf[BeanItem[Schedule]].getBean
    showResult(result)
  }

  private val label = new Label(Page.message("schedules.intro"))
  label.addStyleName(Sizes.tabIntro)

  ui.addComponent(addButton)
  ui.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT)
  ui.addComponent(label)
  ui.addComponent(grid)

  def showResults(results: Seq[Schedule]): Unit = {
    ds.removeAllItems()
    ds.addAll(results.asJava)
    grid.setSortOrder(List(new SortOrder("configLabel", SortDirection.ASCENDING)).asJava)
  }
}
