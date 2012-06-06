package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.configeditor.*;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditor extends TwoColumnsConfigEditor {

    private OtherJiraFieldsPanel jiraFieldsPanel;
    private CustomFieldsTablePanel customFieldsTablePanel;

    public JiraEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    private void buildUI() {
        // top left and right columns
        createServerAndProjectPanelOnTopDefault(new JiraProjectProcessor(this));

        // left column
        jiraFieldsPanel = new OtherJiraFieldsPanel(this, getJiraConfig());
        addToLeftColumn(jiraFieldsPanel);

		PriorityPanel priorityPanel = new PriorityPanel(this, JiraDescriptor.instance, services.getPluginManager());
        addToLeftColumn(priorityPanel);

        // right column
        addToRightColumn(createCustomOtherFieldsPanel());
        addToRightColumn(new FieldsMappingPanel(JiraDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }

    private CustomFieldsTablePanel createCustomOtherFieldsPanel() {
        this.customFieldsTablePanel = new CustomFieldsTablePanel(getJiraConfig().getCustomFields());
        return this.customFieldsTablePanel;
    }

    /**
     * To be used in child panel
     * @return pure config instance
     */
    public JiraConfig getJiraConfig() {
        return (JiraConfig) config;
    }
}
