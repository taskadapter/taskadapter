package com.taskadapter.webui.pages

import java.util.UUID

import com.taskadapter.web.SettingsManager
import com.taskadapter.web.uiapi.{ConfigId, Schedule}
import com.taskadapter.webui._
import com.vaadin.data.sort.SortOrder
import com.vaadin.data.util.{BeanItem, BeanItemContainer}
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui._
import org.slf4j.LoggerFactory

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

case class ScheduleListItem(id: String, configId: ConfigId,
                            @BeanProperty configLabel: String,
                            @BeanProperty intervalMin: Int,
                            @BeanProperty to: String)

class SchedulesListPage(tracker: Tracker, schedulesStorage: SchedulesStorage, configOperations: ConfigOperations,
                        settingsManager: SettingsManager) {
  private val log = LoggerFactory.getLogger(classOf[SchedulesListPage])
  private val configRowsToShowInListSelect = 15

  val ds = new BeanItemContainer[ScheduleListItem](classOf[ScheduleListItem])
  val grid = new Grid(ds)

  val ui = new VerticalLayout()
  val listLayout = createListLayout()
  val configsListSelect = createConfigsList()

  ui.addComponent(listLayout)

  def showSchedules(results: Seq[Schedule]): Unit = {
    ds.removeAllItems()
    ds.addAll(
      results.flatMap { schedule =>
        val maybeConfig = configOperations.getOwnedConfigs.find(c => c.id == schedule.configId)
        if (maybeConfig.isDefined) {
          Some(ScheduleListItem(schedule.id, schedule.configId, maybeConfig.get.label,
            schedule.intervalInMinutes, maybeConfig.get.getConnector2.getLabel))
        } else {
          log.error(s"cannot find config for schedule $schedule")
          None
        }
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
    val config = configOperations.getOwnedConfigs.find(c => c.id == schedule.configId).get
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

  def getSelectedConfigId(): ConfigId = {
    configsListSelect.getValue.asInstanceOf[ConfigId]
  }

  def showSelectConfig(): Unit = {
    val layout = new VerticalLayout()
    layout.setSpacing(true)
    val goButton = new Button(Page.message("schedules.newConfig.go"))
    goButton.addClickListener(_ => showNewScheduleEditor(getSelectedConfigId()))

    val label = new Label(Page.message("schedules.selectConfig.title"))
    label.addStyleName(Sizes.tabIntro)
    layout.addComponent(label)
    val horizontalLayout = new HorizontalLayout(configsListSelect, goButton)
    horizontalLayout.setSpacing(true)
    layout.addComponent(horizontalLayout)

    reloadConfigsInList()
    applyUI(layout)
  }

  private def createConfigsList(): ListSelect = {
    val res = new ListSelect()
    res.setNullSelectionAllowed(false)
    res
  }

  private def reloadConfigsInList(): Unit = {
    configOperations.getOwnedConfigs.foreach { s =>
      configsListSelect.addItem(s.id)
      configsListSelect.setItemCaption(s.id, s.label)
    }
    val rowsNumber = if (configsListSelect.size < configRowsToShowInListSelect) {
      configsListSelect.size
    } else {
      configRowsToShowInListSelect
    }
    configsListSelect.setRows(rowsNumber)
    if (configsListSelect.getRows > 0) {
      configsListSelect.select(configsListSelect.getItemIds.iterator().next())
    }
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
