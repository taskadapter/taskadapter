package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.Label
import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import java.util.UUID

import com.taskadapter.web.uiapi.{ConfigId, Schedule}
import com.taskadapter.webui._
import com.taskadapter.webui.service.Preservices
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

class SchedulesListPage() extends BasePage {
  private val configOps: ConfigOperations = SessionController.buildConfigOperations()
  private val services: Preservices = SessionController.getServices

  EventTracker.trackPage("schedules");
  private val log = LoggerFactory.getLogger(classOf[SchedulesListPage])
  private val configRowsToShowInListSelect = 15

  val ds = new BeanItemContainer[ScheduleListItem](classOf[ScheduleListItem])
  val grid = new Grid(ds)

  val ui = new VerticalLayout()
  val listLayout = createListLayout()
  val configsListSelect = createConfigsList()

  ui.addComponent(listLayout)
  showSchedules(services.schedulesStorage.getSchedules())

  def showSchedules(results: Seq[Schedule]): Unit = {
    ds.removeAllItems()
    ds.addAll(
      results.flatMap { schedule =>
        val maybeConfig = configOps.getOwnedConfigs.find(c => c.configId == schedule.configId)
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
    showSchedules(services.schedulesStorage.getSchedules())
  }

  private def applyUI(newUi: Component): Unit = {
    ui.removeAll()
    ui.add(newUi)
  }

  private def showSchedule(scheduleId: String): Unit = {
    EventTracker.trackPage("edit_schedule_page")
    val schedule = services.schedulesStorage.get(scheduleId).get
    showSchedule(schedule)
  }

  private def showNewScheduleEditor(configId: ConfigId): Unit = {
    EventTracker.trackPage("create_schedule_page")
    val schedule = Schedule(UUID.randomUUID().toString, configId, 60, false, false)
    showSchedule(schedule)
  }

  private def showSchedule(schedule: Schedule): Unit = {
    val config = configOps.getOwnedConfigs.find(c => c.configId == schedule.configId).get
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
    services.schedulesStorage.store(schedule)
    showSchedules()
  }

  def deleteSchedule(schedule: Schedule): Unit = {
    services.schedulesStorage.delete(schedule.id)
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
    label.addClassName(Sizes.tabIntro)
    layout.add(label)
    val horizontalLayout = new HorizontalLayout(configsListSelect, goButton)
    horizontalLayout.setSpacing(true)
    layout.add(horizontalLayout)

    reloadConfigsInList()
    applyUI(layout)
  }

  private def createConfigsList(): ListSelect = {
    val res = new ListSelect()
    res.setNullSelectionAllowed(false)
    res
  }

  private def reloadConfigsInList(): Unit = {
    configOps.getOwnedConfigs.foreach { s =>
      configsListSelect.addItem(s.configId)
      configsListSelect.setItemCaption(s.configId, if (s.label.isEmpty) {
        Page.message("schedules.configsList.defaultLabelForConfigs")
      } else {
        s.label
      }
      )
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
    checkbox.setValue(services.settingsManager.schedulerEnabled)
    checkbox.setImmediate(true)
    checkbox.addValueChangeListener(_ => services.settingsManager.setSchedulerEnabled(checkbox.getValue))

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
      val row = grid.getSelectedRow
      if (row != null) {
        val result = grid.getContainerDataSource.getItem(row).asInstanceOf[BeanItem[ScheduleListItem]].getBean
        showSchedule(result.id)
      }
    }

    val label = new Label(Page.message("schedules.intro"))
    label.addClassName(Sizes.tabIntro)

    val controlsLayout = new HorizontalLayout(addButton, checkbox)
    controlsLayout.setWidth("100%")
    controlsLayout.setSpacing(true)
    controlsLayout.setComponentAlignment(addButton, Alignment.MIDDLE_LEFT)
    controlsLayout.setComponentAlignment(checkbox, Alignment.MIDDLE_RIGHT)

    val listLayout = new VerticalLayout()
    listLayout.add(label)
    listLayout.add(controlsLayout)
    listLayout.add(grid)
    listLayout
  }

}
