package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.CustomField;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.LookupOperation;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

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
        addFieldsMappingPanel(JiraDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());
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
        private CustomFieldsPanel customFieldsPanel;
        private JiraEditor jiraEditor;

        public OtherJiraFieldsPanel(JiraEditor jiraEditor) {
            this.jiraEditor = jiraEditor;
            buildUI();
            setDataToForm();
        }

        private void buildUI() {
            setColumns(3);
            setCaption(SAVE_GROUP_LABEL);
            this.jiraComponent = EditorUtil.addLabeledText(this, "Project Component", "Component inside the Jira project");

            LookupOperation loadComponentsOperation = new LoadComponentsOperation(jiraEditor, new JiraFactory());
            addComponent(EditorUtil.createLookupButton(jiraEditor,
                    "...",
                    "Show list of available components on the given server.",
                    loadComponentsOperation, jiraComponent, true));

            this.affectedVersion = EditorUtil.addLabeledText(this, "Set 'Affected version' to:", "Set this 'affected version' value when submitting issues to Jira.");
            LoadVersionsOperation loadVersionsOperation = new LoadVersionsOperation(jiraEditor, new JiraFactory());
            addComponent(EditorUtil.createLookupButton(jiraEditor,
                    "...",
                    "Show list of available versions.",
                    loadVersionsOperation, affectedVersion, true));

            this.fixForVersion = EditorUtil.addLabeledText(this, "Set 'Fix for version' to:", "Set this 'fix for version' value when submitting issues to Jira.");
            addComponent(EditorUtil.createLookupButton(jiraEditor,
                    "...",
                    "Show list of available versions.",
                    loadVersionsOperation, fixForVersion, true));

            this.defaultTaskType = EditorUtil.addLabeledText(this, "Default issue type:", "New issues will be created with this 'issue type' (bug/improvement/task...)");
            addComponent(EditorUtil.createLookupButton(jiraEditor,
                    "...",
                    "Show list of available issue types on the Jira server",
                    new LoadIssueTypesOperation(jiraEditor, new JiraFactory()), defaultTaskType, true));

            customFieldsPanel = new CustomFieldsPanel();
            addComponent(customFieldsPanel);

        }

        private void setDataToForm() {
            JiraConfig jiraConfig = (JiraConfig) config;
            EditorUtil.setNullSafe(affectedVersion, jiraConfig.getAffectedVersion());
            EditorUtil.setNullSafe(fixForVersion, jiraConfig.getFixForVersion());
            EditorUtil.setNullSafe(jiraComponent, jiraConfig.getComponent());
            EditorUtil.setNullSafe(defaultTaskType, config.getDefaultTaskType());
            Map<String, String> trackers = jiraConfig.getCustomFields();
            for (String key : trackers.keySet()) {
                customFieldsPanel.addTrackerOnTable(key, trackers.get(key));
            }
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
            return customFieldsPanel.getCustomFields();
        }
        
        public String getDefaultTaskType() {
            return (String) defaultTaskType.getValue();
        }
    }

}