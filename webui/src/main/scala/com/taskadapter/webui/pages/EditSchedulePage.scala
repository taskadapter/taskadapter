package com.taskadapter.webui.pages

import com.taskadapter.web.uiapi.Schedule
import com.taskadapter.webui.Page
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui._

class EditSchedulePage(configLabel: String,
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

    val scheduledLeftField = new CheckBox(Page.message("export.schedule.left"),
      new MethodProperty[Boolean](schedule, "directionLeft"))
    val scheduledRightField = new CheckBox(Page.message("export.schedule.right"),
      new MethodProperty[Boolean](schedule, "directionRight"))

    val form = new FormLayout
    form.setWidth("400px")
    val labelField = new TextField(Page.message("editSchedule.configLabel"), configLabel)
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
    buttonsLayout.addComponent(saveButton)
    buttonsLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_LEFT)

    buttonsLayout.addComponent(closeButton)
    buttonsLayout.setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER)

    //    if (schedule.) {
    buttonsLayout.addComponent(deleteButton)
    buttonsLayout.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT)
    //    }
    buttonsLayout
  }

  layout.addComponent(createButtonsLayout())
  layout.addComponent(createScheduledSyncPanel())

  def ui = layout
}
