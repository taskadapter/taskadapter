package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.Editors;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class OtherRedmineFieldsContainer extends Panel {
    private static final String OTHER_PANEL_CAPTION = "Additional Info";
    private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

    private final RedmineConfig config;
    private WebConnectorSetup setup;
    private final ExceptionFormatter exceptionFormatter;
    private final VerticalLayout view;

    public OtherRedmineFieldsContainer(RedmineConfig config, WebConnectorSetup setup, ExceptionFormatter exceptionFormatter) {
        super(OTHER_PANEL_CAPTION);
        this.config = config;
        this.setup = setup;
        this.exceptionFormatter = exceptionFormatter;
        view = new VerticalLayout();
        buildUI();
    }

    private void buildUI() {

        setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        setHeight("157px");
        view.addComponent(Editors.createFindUsersElement(new MethodProperty<>(config, "findUserByName")));
        final CheckBox saveRelations = new CheckBox(SAVE_ISSUE_LABEL);
        saveRelations.setPropertyDataSource(new MethodProperty<Boolean>(config, "saveIssueRelations"));
        view.addComponent(saveRelations);
        addDefaultTaskTypeElement();
        setContent(view);
    }

    private void addDefaultTaskTypeElement() {
        GridLayout grid = new GridLayout(3, 1);
        grid.setSpacing(true);

        TextField defaultTaskType = EditorUtil.addLabeledText(grid, "Default task type:", "New tasks will be created with this 'tracker' (bug/task/support/feature/...)");
        defaultTaskType.setWidth("200px");
        final MethodProperty<String> taskTypeProperty = new MethodProperty<>(config, "defaultTaskType");
        defaultTaskType.setPropertyDataSource(taskTypeProperty);

        Button showTaskTypesButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available tracker types on the Redmine server",
                "Select task type",
                "List of available task types on the Redmine server",
                () -> RedmineLoaders.loadTrackers(config, setup),
                exceptionFormatter,
                namedKeyedObject -> {
                    taskTypeProperty.setValue(namedKeyedObject.getName());
                    return null;
                }
        );

        grid.addComponent(showTaskTypesButton);
        grid.setComponentAlignment(showTaskTypesButton, Alignment.MIDDLE_CENTER);
        view.addComponent(grid);
    }
}