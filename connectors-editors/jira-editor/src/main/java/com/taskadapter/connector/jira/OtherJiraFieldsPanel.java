package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Panel with title: "Set when exporting to JIRA"
 */
class OtherJiraFieldsPanel extends FormLayout {
    private final Binder<JiraConfig> binder;
    private final JiraConfig config;
    private final WebConnectorSetup webServerInfo;
    private final ExceptionFormatter exceptionFormatter;

    public OtherJiraFieldsPanel(Binder<JiraConfig> binder,
                                JiraConfig config, WebConnectorSetup webServerInfo, ExceptionFormatter exceptionFormatter) {
        this.binder = binder;
        this.config = config;
        this.webServerInfo = webServerInfo;
        this.exceptionFormatter = exceptionFormatter;
        buildUI();
    }

    private void buildUI() {
        setResponsiveSteps(
                new FormLayout.ResponsiveStep("20em", 1),
                new FormLayout.ResponsiveStep("20em", 2),
                new FormLayout.ResponsiveStep("5em", 3));

        Label setAffectedVersionLabel = new Label("Set 'Affected version' to");
        setAffectedVersionLabel.getElement().setProperty("title",
                "Set this 'affected version' value when submitting issues to JIRA."); // tooltip
        TextField affectedVersion = EditorUtil.textInput(binder, "affectedVersion");
        Button showAffectedVersion = EditorUtil.createLookupButton(
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                () -> new JiraConnector(config, webServerInfo).getVersions(),
                exceptionFormatter,
                namedKeyedObject -> {
                    affectedVersion.setValue(namedKeyedObject.getName());
                    return null;
                }
        );

        Label fixForVersionLabel = new Label("Set 'Fix for version' to");
        fixForVersionLabel.getElement().setProperty("title",
                "Set this 'fix for version' value when submitting issues to JIRA.");
        TextField fixForVersion = EditorUtil.textInput(binder, "fixForVersion");
        Button showFixForVersion = EditorUtil.createLookupButton(
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                () -> new JiraConnector(config, webServerInfo).getVersions(),
                exceptionFormatter,
                namedKeyedObject -> {
                    fixForVersion.setValue(namedKeyedObject.getName());
                    return null;
                }
        );

        Label defaultTaskTypeLabel = new Label("Default issue type");
        fixForVersionLabel.getElement().setProperty("title",
                "New issues will be created with this issue type (bug/improvement/task...)");
        TextField defaultTaskTypeField = EditorUtil.textInput(binder, "defaultTaskType");
        Button showDefaultTaskType = EditorUtil.createLookupButton(
                "...",
                "Show list of available issue types on the JIRA server",
                "Select issue type",
                "List of available issue types on the JIRA server",
                () -> new JiraConnector(config, webServerInfo).getAllIssueTypes(),
                exceptionFormatter,
                namedKeyedObject -> {
                    defaultTaskTypeField.setValue(namedKeyedObject.getName());
                    return null;
                }
        );

        Label issueTypeSubtasksLabel = new Label("Default issue type for subtasks");
        issueTypeSubtasksLabel.getElement().setProperty("title",
                "Subtasks will be created with this issue type (typically this is 'subtask')");
        TextField defaultIssueTypeForSubtasks = EditorUtil.textInput(binder, "defaultIssueTypeForSubtasks");
        Button showIssueTypeForSubtasksButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available subtask types on the JIRA server",
                "Select a subtask type",
                "List of available subtask types on the JIRA server",
                () -> new JiraConnector(config, webServerInfo).getIssueTypesForSubtasks(),
                exceptionFormatter,
                namedKeyedObject -> {
                    defaultIssueTypeForSubtasks.setValue(namedKeyedObject.getName());
                    return null;
                }
        );

        add(setAffectedVersionLabel, affectedVersion, showAffectedVersion,
                fixForVersionLabel, fixForVersion, showFixForVersion,
                defaultTaskTypeLabel, defaultTaskTypeField, showDefaultTaskType,
                issueTypeSubtasksLabel, defaultIssueTypeForSubtasks, showIssueTypeForSubtasksButton);
    }
}
