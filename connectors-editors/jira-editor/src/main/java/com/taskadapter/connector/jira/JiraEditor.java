package com.taskadapter.connector.jira;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.*;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.*;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Panel;

import java.util.List;

public class JiraEditor extends TwoColumnsConfigEditor {

    public JiraEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        // left column
        OtherJiraFieldsPanel jiraFieldsPanel = new OtherJiraFieldsPanel(this, getJiraConfig());
        addToLeftColumn(jiraFieldsPanel);

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, this, "loadJiraPriorities"));
        addToLeftColumn(priorityPanel);

        // right column
        addToRightColumn(createCustomOtherFieldsPanel());
    }

    /**
     * Loads jira priorities.
     *
     * @return priorities from server.
     * @throws ConnectorException
     */
    Priorities loadJiraPriorities() throws ValidationException, ConnectorException {
        if (!getJiraConfig().getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
        return JiraLoaders.loadPriorities(getJiraConfig().getServerInfo());
    }

    private CustomFieldsTablePanel createCustomOtherFieldsPanel() {
        CustomFieldsTablePanel customFieldsTablePanel = new CustomFieldsTablePanel(getJiraConfig().getCustomFields());
        return customFieldsTablePanel;
    }

    private JiraConfig getJiraConfig() {
        return (JiraConfig) config;
    }
}
