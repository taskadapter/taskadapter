package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class OtherRedmineFieldsContainer extends VerticalLayout {
    private static final String OTHER_PANEL_CAPTION = "Additional Info";
    private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

    private final RedmineConfig config;
    private WebConnectorSetup setup;
    private final ExceptionFormatter exceptionFormatter;
    private final Binder<RedmineConfig> binder;

    public OtherRedmineFieldsContainer(Binder<RedmineConfig> binder,
                                       RedmineConfig config, WebConnectorSetup setup, ExceptionFormatter exceptionFormatter) {
        this.binder = binder;
        this.config = config;
        this.setup = setup;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        Checkbox findUserByName = EditorUtil.checkbox("Find users based on assignee's name",
                "This option can be useful when you need to export a new MSP project file to Redmine/JIRA/MantisBT/....\n"
                        + "Task Adapter can load the system's users by resource names specified in the MSP file\n"
                        + "and assign the new tasks to them.\n"
                        + "Note: this operation usually requires 'Admin' permission in the system.",
                binder, "findUserByName");

        Checkbox saveRelations = EditorUtil.checkbox(SAVE_ISSUE_LABEL,
                "",
                binder, "saveIssueRelations");

        add(findUserByName,
                saveRelations,
                createDefaultTaskTypeElement());
    }

    private Component createDefaultTaskTypeElement() {
        HorizontalLayout row = new HorizontalLayout();

        Label label = new Label("Default task type");
        label.getElement().setProperty("title",
                "New tasks will be created with this 'tracker' (bug/task/support/feature/...)"); // tooltip

        TextField textField = EditorUtil.textInput(binder, "defaultTaskType");

        Button showTaskTypesButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available tracker types on the Redmine server",
                "Select task type",
                "List of available task types on the Redmine server",
                () -> RedmineLoaders.loadTrackers(config, setup),
                exceptionFormatter,
                namedKeyedObject -> {
                    textField.setValue(namedKeyedObject.getName());
                    return null;
                }
        );

        row.add(label, textField, showTaskTypesButton);
        return row;
    }
}