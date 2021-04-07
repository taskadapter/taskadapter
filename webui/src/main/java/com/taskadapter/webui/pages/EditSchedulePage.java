package com.taskadapter.webui.pages;

import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.uiapi.Schedule;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.util.function.Function;

public class EditSchedulePage extends VerticalLayout {
    private final String configLabel;
    private final String destinationLeft;
    private final String destinationRight;
    private final Schedule schedule;
    private final Function<Schedule, Void> save;
    private final Runnable close;
    private final Function<Schedule, Void> delete;

    private final Binder<Schedule> binder = new Binder<>(Schedule.class);

    public EditSchedulePage(String configLabel, String destinationLeft, String destinationRight, Schedule schedule,
                            Function<Schedule, Void> save,
                            Runnable close,
                            Function<Schedule, Void> delete) {
        this.configLabel = configLabel;
        this.destinationLeft = destinationLeft;
        this.destinationRight = destinationRight;
        this.schedule = schedule;
        this.save = save;
        this.close = close;
        this.delete = delete;

        setSpacing(true);

        add(createButtonsLayout(), createScheduledSyncPanel());
    }

    private Component createScheduledSyncPanel() {
        var intervalLabel = new Label(Page.message("export.schedule.runIntervalInMinutes"));
        var runIntervalField = new TextField();
        binder.forField(runIntervalField)
                .withConverter(new StringToIntegerConverter("Not a number"))
                .withNullRepresentation(0)
                .bind("intervalInMinutes");

        var scheduledLeftField = EditorUtil.checkbox(Page.message("export.schedule.exportTo", destinationLeft),
                "", binder, "directionLeft");
        var scheduledRightField = EditorUtil.checkbox(Page.message("export.schedule.exportTo", destinationRight),
                "", binder, "directionRight");

        var labelLabel = new Label(Page.message("editSchedule.configLabel"));
        var labelField = new Label(configLabel);

        var form = new FormLayout();
        form.setWidth("700px");

        form.add(labelLabel, labelField);
        form.add(intervalLabel, runIntervalField);
        form.add(scheduledLeftField, 2);
        form.add(scheduledRightField, 2);

        binder.readBean(schedule);
        return new VerticalLayout(form);
    }

    private Component createButtonsLayout() {
        var saveButton = new Button(Page.message("editSchedulePage.saveButton"),
                event -> {
                    try {
                        binder.writeBean(schedule);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                    save.apply(schedule);
                });
        var closeButton = new Button(Page.message("editSchedulePage.closeButton"),
                event -> close.run());

        var deleteButton = new Button(Page.message("editSchedulePage.deleteButton"),
                event -> delete.apply(schedule));

        var buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.setWidth("100%");
        buttonsLayout.add(saveButton);

        buttonsLayout.add(closeButton);
        buttonsLayout.add(deleteButton);
        return buttonsLayout;
    }

}
