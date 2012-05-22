package com.taskadapter.connector.jira;

import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

/**
* Panel with title: "Set these fields when EXPORTING to Jira"
*/
class OtherJiraFieldsPanel extends Panel {
    private static final String SAVE_GROUP_LABEL = "Set these fields when EXPORTING to Jira";

    private TextField jiraComponent;
    private TextField affectedVersion;
    private TextField fixForVersion;
    private TextField defaultTaskType;
    private JiraEditor jiraEditor;

    public OtherJiraFieldsPanel(JiraEditor jiraEditor) {
        this.jiraEditor = jiraEditor;
        buildUI();
        setDataToForm();
    }

    private void buildUI() {
        setCaption(SAVE_GROUP_LABEL);

        GridLayout lookupButtonsLayout = new GridLayout(3, 4);
        lookupButtonsLayout.setMargin(true);
        lookupButtonsLayout.setSpacing(true);
        addLookupButtonsAndTextEdit(lookupButtonsLayout);

        addComponent(lookupButtonsLayout);
    }

    private void addLookupButtonsAndTextEdit(GridLayout lookupButtonsLayout) {
        jiraComponent = EditorUtil.addLabeledText(lookupButtonsLayout, "Project Component", "Component inside the Jira project");
        Button showComponentsButton = EditorUtil.createLookupButton(
                jiraEditor,
                "...",
                "Show list of available components on the given server.",
                "Select component",
                "List of available components on the server",
                new LoadComponentsOperation(jiraEditor, new JiraFactory()),
                jiraComponent,
                true
        );
        lookupButtonsLayout.addComponent(showComponentsButton);


        LoadVersionsOperation loadVersionsOperation = new LoadVersionsOperation(jiraEditor, new JiraFactory());

        affectedVersion = EditorUtil.addLabeledText(lookupButtonsLayout, "Set 'Affected version' to:", "Set this 'affected version' value when submitting issues to Jira.");
        Button showAffectedVersion = EditorUtil.createLookupButton(
                jiraEditor,
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                loadVersionsOperation,
                affectedVersion,
                true
        );
        lookupButtonsLayout.addComponent(showAffectedVersion);

        fixForVersion = EditorUtil.addLabeledText(lookupButtonsLayout, "Set 'Fix for version' to:", "Set this 'fix for version' value when submitting issues to Jira.");
        Button showFixForVersion = EditorUtil.createLookupButton(
                jiraEditor,
                "...",
                "Show list of available versions",
                "Select version",
                "List of available versions",
                loadVersionsOperation,
                fixForVersion,
                true
        );
        lookupButtonsLayout.addComponent(showFixForVersion);


        defaultTaskType = EditorUtil.addLabeledText(lookupButtonsLayout, "Default issue type:", "New issues will be created with this 'issue type' (bug/improvement/task...)");
        Button showDefaultTaskType = EditorUtil.createLookupButton(
                jiraEditor,
                "...",
                "Show list of available issue types on the Jira server",
                "Select issue type",
                "List of available issue types on the Jira server",
                new LoadIssueTypesOperation(jiraEditor, new JiraFactory()),
                defaultTaskType,
                true
        );
        lookupButtonsLayout.addComponent(showDefaultTaskType);
    }

    private void setDataToForm() {
        JiraConfig jiraConfig = jiraEditor.getJiraConfig();

        EditorUtil.setNullSafe(affectedVersion, jiraConfig.getAffectedVersion());
        EditorUtil.setNullSafe(fixForVersion, jiraConfig.getFixForVersion());
        EditorUtil.setNullSafe(jiraComponent, jiraConfig.getComponent());
        EditorUtil.setNullSafe(defaultTaskType, jiraConfig.getDefaultTaskType());
    }

    public String getAffectedVersion() {
        return (String) affectedVersion.getValue();
    }

    public String getFixForVersion() {
        return (String) fixForVersion.getValue();
    }

    public String getComponent() {
        return (String) jiraComponent.getValue();
    }

    public String getDefaultTaskType() {
        return (String) defaultTaskType.getValue();
    }
}
