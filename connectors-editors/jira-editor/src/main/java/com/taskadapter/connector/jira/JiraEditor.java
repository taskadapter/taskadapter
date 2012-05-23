package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditor extends TwoColumnsConfigEditor {

    private OtherJiraFieldsPanel jiraFieldsPanel;
    private CustomFieldsTablePanel customFieldsTablePanel;

    public JiraEditor(ConnectorConfig config) {
        super(config);
        buildUI();
        setData(config);
    }

    private void buildUI() {
        // top left and right columns
        createServerAndProjectPanelOnTopDefault(new JiraProjectProcessor(this));

        // left column
        jiraFieldsPanel = new OtherJiraFieldsPanel(this);
        addToLeftColumn(jiraFieldsPanel);

        priorityPanel = new PriorityPanel(this, JiraDescriptor.instance);
        priorityPanel.setPriorities(config.getPriorities());
        addToLeftColumn(priorityPanel);

        // right column
        addToRightColumn(createCustomOtherFieldsPanel());

        fieldsMappingPanel = new FieldsMappingPanel(JiraDescriptor.instance.getAvailableFieldsProvider(), config);
        addToRightColumn(fieldsMappingPanel);
    }

    private CustomFieldsTablePanel createCustomOtherFieldsPanel() {
        this.customFieldsTablePanel = new CustomFieldsTablePanel();

        final JiraConfig jiraConfig = getJiraConfig();

        final Map<String, String> trackers = jiraConfig.getCustomFields();
        final List<CustomField> customFieldsList = new ArrayList<CustomField>(trackers.size());

        for (String key : trackers.keySet()) {
            customFieldsList.add(new CustomField(key, trackers.get(key)));
        }

        this.customFieldsTablePanel.setCustomFields(customFieldsList);

        return this.customFieldsTablePanel;
    }

    @Override
    public ConnectorConfig getPartialConfig() {
        JiraConfig newConfig = new JiraConfig();

        newConfig.setAffectedVersion(jiraFieldsPanel.getAffectedVersion());
        newConfig.setFixForVersion(jiraFieldsPanel.getFixForVersion());
        newConfig.setComponent(jiraFieldsPanel.getComponent());

        Map<String, String> trackers = new TreeMap<String, String>();

        for (CustomField customField : customFieldsTablePanel.getCustomFields()) {
            trackers.put(customField.getId(), customField.getValue());
        }

        newConfig.setCustomFields(trackers);
        newConfig.setDefaultTaskType(jiraFieldsPanel.getDefaultTaskType());

        return newConfig;
    }

    /**
     * To be used in child panel
     * @return pure config instance
     */
    public JiraConfig getJiraConfig() {
        return (JiraConfig) config;
    }
}