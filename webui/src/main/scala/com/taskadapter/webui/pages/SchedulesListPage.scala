package com.taskadapter.webui.pages

import java.util.UUID

import com.taskadapter.web.SettingsManager
import com.taskadapter.web.uiapi.{ConfigId, Schedule, UISyncConfig}
import com.taskadapter.webui.{Page, SchedulesStorage, Sizes, Tracker}
import com.vaadin.data.sort.SortOrder
import com.vaadin.data.util.{BeanItem, BeanItemContainer}
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui._

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

case class ScheduleListItem(id: String, configId: ConfigId,
                            @BeanProperty configLabel: String,
                            @BeanProperty intervalMin: Int,
                            @BeanProperty to: String)

class SchedulesListPage(tracker: Tracker, schedulesStorage: SchedulesStorage, configsList: Seq[UISyncConfig],
                        settingsManager: SettingsManager) {
  val ds = new BeanItemContainer[ScheduleListItem](classOf[ScheduleListItem])
  val grid = new Grid(ds)

  val ui = new VerticalLayout()
  val listLayout = createListLayout()

  ui.addComponent(listLayout)

  def showSchedules(results: Seq[Schedule]): Unit = {
    ds.removeAllItems()
    ds.addAll(
      results.map { schedule =>
        val config = configsList.find(c => c.id == schedule.configId).get
        ScheduleListItem(schedule.id, schedule.configId, config.label,
          schedule.intervalInMinutes, config.getConnector2.getLabel)
      }.asJava)
    grid.setSortOrder(List(new SortOrder("configLabel", SortDirection.ASCENDING)).asJava)
    applyUI(listLayout)
  }


  def showSchedules(): Unit = {
    showSchedules(schedulesStorage.getSchedules())
  }

  private def applyUI(newUi: Component): Unit = {
    ui.removeAllComponents()
    ui.addComponent(newUi)
  }

  private def showSchedule(scheduleId: String): Unit = {
    tracker.trackPage("edit_schedule_page")
    val schedule = schedulesStorage.get(scheduleId).get
    showSchedule(schedule)
  }

  private def showNewScheduleEditor(configId: ConfigId): Unit = {
    tracker.trackPage("create_schedule_page")
    val schedule = Schedule(UUID.randomUUID().toString, configId, 60, false, false)
    showSchedule(schedule)
  }

  private def showSchedule(schedule: Schedule): Unit = {
    val config = configsList.find(c => c.id == schedule.configId).get
    val editSchedulePage = new EditSchedulePage(
      config.label,
      config.getConnector1.getLabel,
      config.getConnector2.getLabel,
      schedule, (schedule) => saveSchedule(schedule),
      showSchedules,
      (schedule) => deleteSchedule(schedule)
    )
    applyUI(editSchedulePage.ui)
  }

  def saveSchedule(schedule: Schedule): Unit = {
    schedulesStorage.store(schedule)
    showSchedules()
  }

  def deleteSchedule(schedule: Schedule): Unit = {
    schedulesStorage.delete(schedule.id)
    showSchedules()
  }

  def showSelectConfig(): Unit = {
    val layout = new VerticalLayout()
    layout.addComponent(new Label(Page.message("schedules.selectConfig.title")))
    layout.addComponent(createConfigsList())
    applyUI(layout)
  }

  private def createConfigsList(): ListSelect = {
    val res = new ListSelect(Page.message("export.schedule.configsList"))
    res.setNullSelectionAllowed(false)
    res.addValueChangeListener(_ =>
      showNewScheduleEditor(res.getValue.asInstanceOf[ConfigId])
    )
    configsList.foreach { s =>
      res.addItem(s.id)
      res.setItemCaption(s.id, s.label)
    }
    res.setRows(res.size)
    res
  }


  def createListLayout(): Layout = {

    val addButton = new Button(Page.message("schedules.newButton"))
    addButton.addClickListener(_ => showSelectConfig())

    val checkbox = new CheckBox(Page.message("schedules.scheduledEnabled"))
    checkbox.setValue(settingsManager.schedulerEnabled)
    checkbox.setImmediate(true)
    checkbox.addValueChangeListener(_ => settingsManager.setSchedulerEnabled(checkbox.getValue))

    grid.setWidth("980px")
    grid.removeAllColumns()

    grid.addColumn("configLabel")
      .setHeaderCaption(Page.message("schedules.column.label"))
      .setExpandRatio(1)


    grid.addColumn("intervalMin")
      .setHeaderCaption(Page.message("schedules.column.interval"))
      .setExpandRatio(1)

    //    grid.addColumn("to")
    //      .setHeaderCaption(Page.message("schedules.column.to"))
    //      .setExpandRatio(2)

    grid.addSelectionListener { _ =>
      val result = grid.getContainerDataSource.getItem(grid.getSelectedRow).asInstanceOf[BeanItem[ScheduleListItem]].getBean
      showSchedule(result.id)
    }

    val label = new Label(Page.message("schedules.intro"))
    label.addStyleName(Sizes.tabIntro)

    val controlsLayout = new HorizontalLayout(addButton, checkbox)
    controlsLayout.setWidth("100%")
    controlsLayout.setSpacing(true)
    controlsLayout.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT)
    controlsLayout.setComponentAlignment(checkbox, Alignment.MIDDLE_RIGHT)

    val listLayout = new VerticalLayout()
    listLayout.addComponent(label)
    listLayout.addComponent(controlsLayout)
    listLayout.addComponent(grid)
    listLayout
  }

}
