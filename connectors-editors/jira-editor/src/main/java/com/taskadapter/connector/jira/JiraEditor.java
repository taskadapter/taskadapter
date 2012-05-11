package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.CustomField;
import com.taskadapter.web.configeditor.CustomFieldsTable;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.ui.*;

import java.util.*;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditor extends ConfigEditor {

    private OtherJiraFieldsPanel jiraFieldsPanel;

    public JiraEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        setData(config);
    }

    private void buildUI() {
        addServerPanel();
        addProjectPanel(this, new JiraProjectProcessor(this));

        jiraFieldsPanel = new OtherJiraFieldsPanel(this);
        addComponent(jiraFieldsPanel);

        addPriorityPanel(this, JiraDescriptor.instance, config.getPriorities());
        addFieldsMappingPanel(JiraDescriptor.instance.getAvailableFieldsProvider());
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        JiraConfig newConfig = new JiraConfig();

        newConfig.setAffectedVersion(jiraFieldsPanel.getAffectedVersion());
        newConfig.setFixForVersion(jiraFieldsPanel.getFixForVersion());
        newConfig.setComponent(jiraFieldsPanel.getComponent());

        Map<String, String> trackers = new TreeMap<String, String>();

        for (CustomField customField : jiraFieldsPanel.getCustomFields()) {
            trackers.put(customField.getId(), customField.getValue());
        }

        newConfig.setCustomFields(trackers);
        newConfig.setDefaultTaskType(jiraFieldsPanel.getDefaultTaskType());

        return newConfig;
    }

    class OtherJiraFieldsPanel extends GridLayout {
        private static final String SAVE_GROUP_LABEL = "Set these fields when EXPORTING to Jira";

        private TextField jiraComponent;
        private TextField affectedVersion;
        private TextField fixForVersion;
        private TextField defaultTaskType;
        private CustomFieldsTable customFieldsTable;
        private JiraEditor jiraEditor;

        public OtherJiraFieldsPanel(JiraEditor jiraEditor) {
            this.jiraEditor = jiraEditor;
            buildUI();
            setDataToForm();
        }

        private void buildUI() {
            setColumns(2);
            setSpacing(true);

            Panel lookupButtonsPanel = new Panel(SAVE_GROUP_LABEL);

            GridLayout lookupButtonsLayout = new GridLayout(3, 4);
            lookupButtonsLayout.setMargin(true);
            lookupButtonsLayout.setSpacing(true);

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

            lookupButtonsPanel.setContent(lookupButtonsLayout);
            addComponent(lookupButtonsPanel);

            customFieldsTable = new CustomFieldsTable();
            addComponent(customFieldsTable);
            setComponentAlignment(customFieldsTable, Alignment.TOP_RIGHT);
        }

        private void setDataToForm() {
            JiraConfig jiraConfig = (JiraConfig) config;

            EditorUtil.setNullSafe(affectedVersion, jiraConfig.getAffectedVersion());
            EditorUtil.setNullSafe(fixForVersion, jiraConfig.getFixForVersion());
            EditorUtil.setNullSafe(jiraComponent, jiraConfig.getComponent());
            EditorUtil.setNullSafe(defaultTaskType, config.getDefaultTaskType());

            Map<String, String> trackers = jiraConfig.getCustomFields();
            List<CustomField> customFieldsList = new ArrayList<CustomField>(trackers.size());

            for (String key : trackers.keySet()) {
                customFieldsList.add(new CustomField(key, trackers.get(key)));
            }

            customFieldsTable.setCustomFields(customFieldsList);
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

        public Collection<CustomField> getCustomFields() {
            return customFieldsTable.getCustomFields();
        }

        public String getDefaultTaskType() {
            return (String) defaultTaskType.getValue();
        }
    }
}