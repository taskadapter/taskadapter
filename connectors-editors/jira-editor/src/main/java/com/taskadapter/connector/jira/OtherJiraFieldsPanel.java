package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

/**
 * Panel with title: "Set when exporting to JIRA"
 */
class OtherJiraFieldsPanel extends Panel {
    private static final String SAVE_GROUP_LABEL = "Set when exporting to JIRA";

    private final JiraConfig config;
    private WebConnectorSetup webServerInfo;
    private final ExceptionFormatter exceptionFormatter;

    public OtherJiraFieldsPanel(JiraConfig config, WebConnectorSetup webServerInfo, ExceptionFormatter exceptionFormatter) {
        this.config = config;
        this.webServerInfo = webServerInfo;
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
        final TextField affectedVersion = EditorUtil
                .addLabeledText(grid,
                        "Set 'Affected version' to:",
                        "Set this 'affected version' value when submitting issues to JIRA.");
        final MethodProperty<String> affectedVersionProperty = new MethodProperty<>(config, "affectedVersion");
        affectedVersion.setPropertyDataSource(affectedVersionProperty);
        Button showAffectedVersion = EditorUtil.createLookupButton(
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                () -> new JiraConnector(config, webServerInfo).getVersions(),
                exceptionFormatter,
                namedKeyedObject -> {
                    affectedVersionProperty.setValue(namedKeyedObject.getName());
                    return null;
                }
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
                () -> new JiraConnector(config, webServerInfo).getVersions(),
                exceptionFormatter,
                namedKeyedObject -> {
                    fixForProperty.setValue(namedKeyedObject.getName());
                    return null;
                }
        );
        grid.addComponent(showFixForVersion);


        final TextField defaultTaskType = EditorUtil.addLabeledText(grid, "Default issue type:",
                        "New issues will be created with this issue type (bug/improvement/task...)");
        final MethodProperty<String> defaultTaskTypeProperty = new MethodProperty<>(config, "defaultTaskType");
        defaultTaskType.setPropertyDataSource(defaultTaskTypeProperty);
        Button showDefaultTaskType = EditorUtil.createLookupButton(
                "...",
                "Show list of available issue types on the JIRA server",
                "Select issue type",
                "List of available issue types on the JIRA server",
                () -> new JiraConnector(config, webServerInfo).getAllIssueTypes(),
                exceptionFormatter,
                namedKeyedObject -> {
                    defaultTaskTypeProperty.setValue(namedKeyedObject.getName());
                    return null;
                }
        );
        grid.addComponent(showDefaultTaskType);

        final TextField defaultIssueTypeForSubtasks = EditorUtil
                .addLabeledText(grid, "Default issue type for subtasks:",
                        "Subtasks will be created with this issue type (typically this is 'subtask')");
        final MethodProperty<String> defaultIssueTypeForSubtasksProperty = new MethodProperty<>(config, "defaultIssueTypeForSubtasks");
        defaultIssueTypeForSubtasks.setPropertyDataSource(defaultIssueTypeForSubtasksProperty);
        Button showIssueTypeForSubtasksButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available subtask types on the JIRA server",
                "Select a subtask type",
                "List of available subtask types on the JIRA server",
                () -> new JiraConnector(config, webServerInfo).getIssueTypesForSubtasks(),
                exceptionFormatter,
                namedKeyedObject -> {
                    defaultIssueTypeForSubtasksProperty.setValue(namedKeyedObject.getName());
                    return null;
                }
        );
        grid.addComponent(showIssueTypeForSubtasksButton);
    }
}
