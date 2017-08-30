package com.taskadapter.webui.pages

import com.taskadapter.web.uiapi.Schedule
import com.taskadapter.webui.Page
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui._

class EditSchedulePage(schedule: Option[Schedule], save: Runnable) {

  val layout = new VerticalLayout
  layout.setSpacing(true)

  private def createScheduledSyncPanel(): Component = {
    val layout = new HorizontalLayout()
    layout.setMargin(true)
    layout.setSpacing(true)
    val runIntervalField = new TextField(Page.message("export.schedule.runIntervalInMinutes"),
      new MethodProperty[Int](schedule, "intervalInMinutes"))

    val scheduledLeftField = new CheckBox(Page.message("export.schedule.left"),
      new MethodProperty[Boolean](schedule, "directionLeft"))
    val scheduledRightField = new CheckBox(Page.message("export.schedule.right"),
      new MethodProperty[Boolean](schedule, "directionRight"))

    layout.addComponent(runIntervalField)
    layout.addComponent(scheduledLeftField)
    layout.addComponent(scheduledRightField)
    new Panel(layout)
  }

  val saveButton = new Button(Page.message("editSchedulePage.saveButton"))
  saveButton.addClickListener(_ => save.run())

  layout.addComponent(saveButton)
  layout.setComponentAlignment(saveButton, Alignment.MIDDLE_LEFT)
  layout.addComponent(createScheduledSyncPanel())

  def ui = layout
}
