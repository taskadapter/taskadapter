package com.taskadapter.webui.pages

import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.web.uiapi.Schedule
import com.taskadapter.webui.Page
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui._

class EditSchedulePage(configLabel: String,
                       destinationLeft: String,
                       destinationRight: String,
                       schedule: Schedule,
                       save: (Schedule) => Unit,
                       close: () => Unit,
                       delete: (Schedule) => Unit
                      ) {

  val layout = new VerticalLayout
  layout.setSpacing(true)

  private def createScheduledSyncPanel(): Component = {
    val runIntervalField = new TextField(Page.message("export.schedule.runIntervalInMinutes"),
      new MethodProperty[Int](schedule, "intervalInMinutes"))

    val scheduledLeftField = new CheckBox(Page.message("export.schedule.exportTo", destinationLeft),
      new MethodProperty[Boolean](schedule, "directionLeft"))
    val scheduledRightField = new CheckBox(Page.message("export.schedule.exportTo", destinationRight),
      new MethodProperty[Boolean](schedule, "directionRight"))

    val form = new FormLayout
    form.setWidth("600px")
    val labelField = new TextField(Page.message("editSchedule.configLabel"), configLabel)
    labelField.setWidth("300px")
    labelField.setReadOnly(true)
    form.addComponent(labelField)
    form.addComponent(runIntervalField)
    form.addComponent(scheduledLeftField)
    form.addComponent(scheduledRightField)
    new Panel(form)
  }

  def createButtonsLayout(): _root_.com.vaadin.ui.Component = {
    val saveButton = new Button(Page.message("editSchedulePage.saveButton"))
    saveButton.addClickListener(_ => save(schedule))
    val closeButton = new Button(Page.message("editSchedulePage.closeButton"))
    closeButton.addClickListener(_ => close())
    val deleteButton = new Button(Page.message("editSchedulePage.deleteButton"))
    deleteButton.addClickListener(_ => delete(schedule))


    val buttonsLayout = new HorizontalLayout()
    buttonsLayout.setSpacing(true)
    buttonsLayout.setWidth("100%")
    buttonsLayout.add(saveButton)
    buttonsLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_LEFT)

    buttonsLayout.add(closeButton)
    buttonsLayout.setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER)

    //    if (schedule.) {
    buttonsLayout.add(deleteButton)
    buttonsLayout.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT)
    //    }
    buttonsLayout
  }

  layout.add(createButtonsLayout())
  layout.add(createScheduledSyncPanel())

  def ui = layout
}
