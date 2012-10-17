package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.Editors;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.List;

public class OtherRedmineFieldsContainer extends Panel {
    private static final String OTHER_PANEL_CAPTION = "Additional Info";
    private static final String SAVE_ISSUE_LABEL = "Save issue relations (follows/precedes)";

    private final WindowProvider windowProvider;
    private final RedmineConfig config;

    public OtherRedmineFieldsContainer(WindowProvider windowProvider, RedmineConfig config) {
        super(OTHER_PANEL_CAPTION);
        this.windowProvider = windowProvider;
        this.config = config;
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
        HorizontalLayout taskTypeLayout = new HorizontalLayout();
        taskTypeLayout.setSpacing(true);
        final TextField defaultTaskType = EditorUtil.addLabeledText(taskTypeLayout, "Default task type:",
                "New tasks will be created with this 'tracker' (bug/task/support/feature/...)");
        defaultTaskType.setWidth("200px");
        final MethodProperty<String> taskTypeProperty = new MethodProperty<String>(config, "defaultTaskType");
        defaultTaskType.setPropertyDataSource(taskTypeProperty);

        Button showDefaultTaskType = EditorUtil.createLookupButton(
                windowProvider,
                "...",
                "Show list of available tracker types on the Redmine server",
                "Select task type",
                "List of available task types on the Redmine server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData()
                            throws ValidationException {
                        try {
                            return RedmineLoaders.loadTrackers(config);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                taskTypeProperty,
                true
        );

        taskTypeLayout.addComponent(showDefaultTaskType);
        addComponent(taskTypeLayout);
    }
}