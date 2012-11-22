package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.Editors;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.List;

public class OtherRedmineFieldsContainer extends Panel {
    private static final String OTHER_PANEL_CAPTION = "Additional Info";
    private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

    private final WindowProvider windowProvider;
    private final RedmineConfig config;
    private final ExceptionFormatter exceptionFormatter;

    public OtherRedmineFieldsContainer(WindowProvider windowProvider, RedmineConfig config, ExceptionFormatter exceptionFormatter) {
        super(OTHER_PANEL_CAPTION);
        this.windowProvider = windowProvider;
        this.config = config;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        setHeight("157px");
        addComponent(Editors.createFindUsersElement(new MethodProperty<Boolean>(config, "findUserByName")));
        final CheckBox saveRelations = new CheckBox(SAVE_ISSUE_LABEL);
        saveRelations.setPropertyDataSource(new MethodProperty<Boolean>(config, "saveIssueRelations"));
        addComponent(saveRelations);
        addDefaultTaskTypeElement();
    }

    private void addDefaultTaskTypeElement() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.addComponent(new Label("Default task type:"));

        TextField defaultTaskType = new TextField();
        defaultTaskType.setDescription("New tasks will be created with this 'tracker' (bug/task/support/feature/...)");
        defaultTaskType.setWidth("200px");
        layout.addComponent(defaultTaskType);
        final MethodProperty<String> taskTypeProperty = new MethodProperty<String>(config, "defaultTaskType");
        defaultTaskType.setPropertyDataSource(taskTypeProperty);

        Button showTaskTypesButton = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available tracker types on the Redmine server",
                "Select task type",
                "List of available task types on the Redmine server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                        return RedmineLoaders.loadTrackers(config);
                    }
                },
                taskTypeProperty,
                true, exceptionFormatter
        );

        layout.addComponent(showTaskTypesButton);
        addComponent(layout);
    }
}