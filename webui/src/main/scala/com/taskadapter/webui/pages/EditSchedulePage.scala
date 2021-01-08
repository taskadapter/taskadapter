package com.taskadapter.webui.pages

import com.taskadapter.web.configeditor.EditorUtil
import com.taskadapter.web.uiapi.Schedule
import com.taskadapter.webui.Page
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.converter.StringToIntegerConverter

class EditSchedulePage(configLabel: String,
                       destinationLeft: String,
                       destinationRight: String,
                       schedule: Schedule,
                       save: Function[Schedule, Void],
                       close: Runnable,
                       delete: Function[Schedule, Void],
                      ) {

  val layout = new VerticalLayout
  layout.setSpacing(true)
  val binder = new Binder[Schedule](classOf[Schedule])

  private def createScheduledSyncPanel(): Component = {

    val intervalLabel = new Label(Page.message("export.schedule.runIntervalInMinutes"));
    val runIntervalField = new TextField()
    binder.forField(runIntervalField)
      .withConverter(new StringToIntegerConverter("Not a number"))
      .withNullRepresentation(0)
      .bind("intervalInMinutes");

    val scheduledLeftField = EditorUtil.checkbox(Page.message("export.schedule.exportTo", destinationLeft),
    "", binder, "directionLeft");
    val scheduledRightField = EditorUtil.checkbox(Page.message("export.schedule.exportTo", destinationRight),
    "", binder, "directionRight");

    val labelLabel = new Label(Page.message("editSchedule.configLabel"))
    val labelField = new Label(configLabel)

    val form = new FormLayout
    form.setWidth("700px")

    form.add(labelLabel, labelField)
    form.add(intervalLabel, runIntervalField)
    form.add(scheduledLeftField, 2)
    form.add(scheduledRightField, 2)

    binder.readBean(schedule)
    new VerticalLayout(form)
  }

  def createButtonsLayout(): Component = {
    val saveButton = new Button(Page.message("editSchedulePage.saveButton"))
    saveButton.addClickListener(_ => {
      binder.writeBean(schedule)
      save(schedule)
    })
    val closeButton = new Button(Page.message("editSchedulePage.closeButton"))
    closeButton.addClickListener(_ => close.run())
    val deleteButton = new Button(Page.message("editSchedulePage.deleteButton"))
    deleteButton.addClickListener(_ => delete(schedule))

    val buttonsLayout = new HorizontalLayout()
    buttonsLayout.setSpacing(true)
    buttonsLayout.setWidth("100%")
    buttonsLayout.add(saveButton)

    buttonsLayout.add(closeButton)
    buttonsLayout.add(deleteButton)
    buttonsLayout
  }

  layout.add(createButtonsLayout())
  layout.add(createScheduledSyncPanel())

  def ui = layout
}
