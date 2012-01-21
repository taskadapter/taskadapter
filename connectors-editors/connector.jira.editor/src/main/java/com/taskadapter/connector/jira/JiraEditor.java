package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.LookupOperation;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditor extends ConfigEditor {

    private static final String SAVE_GROUP_LABEL = "Set these fields when EXPORTING to Jira";

    private TextField jiraComponent;
    private TextField affectedVersion;
    private TextField fixForVersion;
    private CustomFieldsPanel customFieldsPanel;

    public JiraEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        addFieldsMappingPanel(JiraDescriptor.instance.getAvailableFieldsProvider(), config.getFieldsMapping());

        setDataToForm();
    }

    private void setDataToForm() {
        JiraConfig jiraConfig = (JiraConfig) config;
        setIfNotNull(affectedVersion, jiraConfig.getAffectedVersion());
        setIfNotNull(fixForVersion, jiraConfig.getFixForVersion());
        setIfNotNull(jiraComponent, jiraConfig.getComponent());
        Map<String, String> trackers = jiraConfig.getCustomFields();
        for (String key : trackers.keySet()) {
            customFieldsPanel.addTrackerOnTable(key, trackers.get(key));
        }
    }

    private void buildUI() {
        addServerPanel();
        addProjectPanel(this, new JiraProjectProcessor(this));

        // SET THESE FIELDS WHEN EXPORTING
        GridLayout saveGroup = new GridLayout();
        saveGroup.setColumns(3);
        saveGroup.setCaption(SAVE_GROUP_LABEL);
        addComponent(saveGroup);

        this.jiraComponent = createLabeledText(saveGroup, "Project Component", "Component inside the Jira project");

        LookupOperation loadComponentsOperation = new LoadComponentsOperation(this, JiraDescriptor.instance);
        saveGroup.addComponent(EditorUtil.createLookupButton(getWindow(),
                "...",
                "Show list of available components on the given server.",
                loadComponentsOperation, jiraComponent, true));

        this.affectedVersion = createLabeledText(saveGroup, "Set 'Affected version' to:", "Set this 'affected version' value when submitting issues to Jira.");
        LoadVersionsOperation loadVersionsOperation = new LoadVersionsOperation(this, JiraDescriptor.instance);
        saveGroup.addComponent(EditorUtil.createLookupButton(getWindow(),
                "...",
                "Show list of available versions.",
                loadVersionsOperation, affectedVersion, true));

        this.fixForVersion = createLabeledText(saveGroup, "Set 'Fix for version' to:", "Set this 'fix for version' value when submitting issues to Jira.");
        saveGroup.addComponent(EditorUtil.createLookupButton(getWindow(),
                "...",
                "Show list of available versions.",
                loadVersionsOperation, fixForVersion, true));

        customFieldsPanel = new CustomFieldsPanel();
        addComponent(customFieldsPanel);
        addPriorityPanel(this, JiraDescriptor.instance, config.getPriorities());
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        JiraConfig newConfig = new JiraConfig();

        newConfig.setAffectedVersion((String) affectedVersion.getValue());
        newConfig.setFixForVersion((String) fixForVersion.getValue());
        newConfig.setComponent((String) jiraComponent.getValue());
        Map<String, String> trackers = new TreeMap<String, String>();
        for (CustomField customField : customFieldsPanel.getCustomFields()) {
            trackers.put(customField.getId(), customField.getValue());
        }
        newConfig.setCustomFields(trackers);
        return newConfig;
    }
}