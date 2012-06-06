package com.taskadapter.connector.jira;

import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

/**
* Panel with title: "Set these fields when EXPORTING to Jira"
*/
class OtherJiraFieldsPanel extends Panel {
    private static final String SAVE_GROUP_LABEL = "Set these fields when EXPORTING to Jira";

    private JiraEditor jiraEditor;
    
    private JiraConfig config;

    public OtherJiraFieldsPanel(JiraEditor jiraEditor, JiraConfig config) {
        this.jiraEditor = jiraEditor;
        this.config = config;
        buildUI();
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
		final TextField jiraComponent = EditorUtil.addLabeledText(
				lookupButtonsLayout, "Project Component:",
				"Component inside the Jira project");
        jiraComponent.setPropertyDataSource(new MethodProperty<String>(config, "component"));
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

		final TextField affectedVersion = EditorUtil
				.addLabeledText(lookupButtonsLayout,
						"Set 'Affected version' to:",
						"Set this 'affected version' value when submitting issues to Jira.");
        affectedVersion.setPropertyDataSource(new MethodProperty<String>(config, "affectedVersion"));
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

		final TextField fixForVersion = EditorUtil
				.addLabeledText(lookupButtonsLayout,
						"Set 'Fix for version' to:",
						"Set this 'fix for version' value when submitting issues to Jira.");
        fixForVersion.setPropertyDataSource(new MethodProperty<String>(config, "fixForVersion"));
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


		final TextField defaultTaskType = EditorUtil
				.addLabeledText(lookupButtonsLayout, "Default issue type:",
						"New issues will be created with this 'issue type' (bug/improvement/task...)");
        defaultTaskType.setPropertyDataSource(new MethodProperty<String>(config, "defaultTaskType"));
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
}
