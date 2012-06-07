package com.taskadapter.connector.jira;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.web.callbacks.DataProvider;
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

		PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(), new DataProvider<Priorities>() {
			@Override
			public Priorities loadData() throws ValidationException {
				return loadJiraPriorities();
			}
		});
        addToLeftColumn(priorityPanel);

        // right column
        addToRightColumn(createCustomOtherFieldsPanel());
        addToRightColumn(new FieldsMappingPanel(JiraDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }

    /**
     * Loads jira priorities.
     * @return priorities from server.
     */
    Priorities loadJiraPriorities() throws ValidationException {
        if (!getJiraConfig().getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
		return JiraLoaders.loadPriorities(getJiraConfig()
				.getServerInfo());
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
