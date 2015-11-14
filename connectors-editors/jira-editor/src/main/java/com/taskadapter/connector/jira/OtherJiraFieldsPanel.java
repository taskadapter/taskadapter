package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.List;

/**
 * Panel with title: "Set when exporting to JIRA"
 */
class OtherJiraFieldsPanel extends Panel {
    private static final String SAVE_GROUP_LABEL = "Set when exporting to JIRA";

    private final JiraConfig config;
    private final ExceptionFormatter exceptionFormatter;

    public OtherJiraFieldsPanel(JiraConfig config, ExceptionFormatter exceptionFormatter) {
        this.config = config;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        setCaption(SAVE_GROUP_LABEL);

        GridLayout lookupButtonsLayout = new GridLayout(3, 4);
        lookupButtonsLayout.setMargin(true);
        lookupButtonsLayout.setSpacing(true);
        addLookupButtonsAndTextEdit(lookupButtonsLayout);

        setContent(lookupButtonsLayout);
    }

    private void addLookupButtonsAndTextEdit(GridLayout grid) {
        final TextField jiraComponent = EditorUtil.addLabeledText(
                grid, "Project Component:",
                "Component in the JIRA project");
        final MethodProperty<String> componentProperty = new MethodProperty<>(config, "component");
        jiraComponent.setPropertyDataSource(componentProperty);
        Button showComponentsButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available components on the given server.",
                "Select component",
                "List of available components on the server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                        return new JiraConnector(config).getComponents();
                    }
                },
                componentProperty,
                true, exceptionFormatter
        );
        grid.addComponent(showComponentsButton);
        grid.setComponentAlignment(showComponentsButton, Alignment.MIDDLE_CENTER);


        final TextField affectedVersion = EditorUtil
                .addLabeledText(grid,
                        "Set 'Affected version' to:",
                        "Set this 'affected version' value when submitting issues to JIRA.");
        final MethodProperty<String> affectedVersionProperty = new MethodProperty<>(config, "affectedVersion");
        affectedVersion.setPropertyDataSource(affectedVersionProperty);
        final DataProvider<List<? extends NamedKeyedObject>> versionProvider = new DataProvider<List<? extends NamedKeyedObject>>() {
            @Override
            public List<? extends NamedKeyedObject> loadData()
                    throws ConnectorException {
                return new JiraConnector(config).getVersions();
            }
        };
        Button showAffectedVersion = EditorUtil.createLookupButton(
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                versionProvider,
                affectedVersionProperty,
                true,
                exceptionFormatter
        );
        grid.addComponent(showAffectedVersion);

        final TextField fixForVersion = EditorUtil
                .addLabeledText(grid,
                        "Set 'Fix for version' to:",
                        "Set this 'fix for version' value when submitting issues to JIRA.");
        final MethodProperty<String> fixForProperty = new MethodProperty<>(config, "fixForVersion");
        fixForVersion.setPropertyDataSource(fixForProperty);
        Button showFixForVersion = EditorUtil.createLookupButton(
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                versionProvider,
                fixForProperty,
                true, exceptionFormatter
        );
        grid.addComponent(showFixForVersion);


        final TextField defaultTaskType = EditorUtil
                .addLabeledText(grid, "Default issue type:",
                        "New issues will be created with this issue type (bug/improvement/task...)");
        final MethodProperty<String> defaultTaskTypeProperty = new MethodProperty<>(config, "defaultTaskType");
        defaultTaskType.setPropertyDataSource(defaultTaskTypeProperty);
        Button showDefaultTaskType = EditorUtil.createLookupButton(
                "...",
                "Show list of available issue types on the JIRA server",
                "Select issue type",
                "List of available issue types on the JIRA server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                        return new JiraConnector(config).getAllIssueTypes();
                    }
                },
                defaultTaskTypeProperty,
                true,
                exceptionFormatter
        );
        grid.addComponent(showDefaultTaskType);

        final TextField defaultIssueTypeForSubtasks = EditorUtil
                .addLabeledText(grid, "Issue type for subtasks:",
                        "Subtasks will be created with this issue type (typically this is 'subtask')");
        final MethodProperty<String> defaultIssueTypeForSubtasksProperty = new MethodProperty<>(config, "defaultIssueTypeForSubtasks");
        defaultIssueTypeForSubtasks.setPropertyDataSource(defaultIssueTypeForSubtasksProperty);
        Button showIssueTypeForSubtasksButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available subtask types on the JIRA server",
                "Select a subtask type",
                "List of available subtask types on the JIRA server",
                new DataProvider<List<? extends NamedKeyedObject>>() {
                    @Override
                    public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                        return new JiraConnector(config).getIssueTypesForSubtasks();
                    }
                },
                defaultIssueTypeForSubtasksProperty,
                true,
                exceptionFormatter
        );
        grid.addComponent(showIssueTypeForSubtasksButton);
    }
}
