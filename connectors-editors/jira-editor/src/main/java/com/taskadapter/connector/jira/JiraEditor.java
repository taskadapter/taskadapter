package com.taskadapter.connector.jira;

import java.util.List;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.*;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;

public class JiraEditor extends TwoColumnsConfigEditor {

    public JiraEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    @SuppressWarnings("unchecked")
	private void buildUI() {
        // top left and right columns
        createServerAndProjectPanelOnTopDefault(
        		EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
        		EditorUtil.wrapNulls(new MethodProperty<Integer>(config, "queryId")),
        		Interfaces.fromMethod(DataProvider.class, JiraLoaders.class, 
        				"loadProjects", getJiraConfig().getServerInfo()),
        		Interfaces.fromMethod(SimpleCallback.class, this, "loadProjectInfo"),
        		Interfaces.fromMethod(DataProvider.class, this, "loadQueries"));

        // left column
        OtherJiraFieldsPanel jiraFieldsPanel = new OtherJiraFieldsPanel(this, getJiraConfig());
        addToLeftColumn(jiraFieldsPanel);

		PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
				Interfaces.fromMethod(DataProvider.class, this, "loadJiraPriorities"));
        addToLeftColumn(priorityPanel);

        // right column
        addToRightColumn(createCustomOtherFieldsPanel());
        addToRightColumn(new FieldsMappingPanel(JiraDescriptor.instance.getAvailableFields(), config.getFieldMappings()));
    }

    /**
     * Loads queries.
     * @return queries to load.
     */
    List<? extends NamedKeyedObject> loadQueries() {
		try {
			return new JiraConnector(getJiraConfig()).getFilters();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
     * Shows a project info.
     * @throws ValidationException 
	 * @throws ConnectorException 
     */
    void loadProjectInfo() throws ValidationException, ConnectorException {
        WebConfig webConfig = (WebConfig) config;
        if (!webConfig.getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
        if (webConfig.getProjectKey() == null || webConfig.getProjectKey().isEmpty()) {
            throw new ValidationException("Please, provide the project key first");
        }
        GProject project = JiraLoaders.loadProject(
                webConfig.getServerInfo(), webConfig.getProjectKey());
        showProjectInfo(project);
		
	}
    
    private void showProjectInfo(GProject project) {
        String msg = "Id: " + project.getId() + "\nKey:  "
                + project.getKey() + "\nName: "
                + project.getName();
        // + "\nLead: " + project.getLead()
        // + "\nURL: " + project.getProjectUrl()
        msg += addNullSafe("Homepage", project.getHomepage());
        msg += addNullSafe("Description", project.getDescription());

        EditorUtil.show(getWindow(), "Project Info", msg);
    }
    
    private String addNullSafe(String label, String fieldValue) {
        String msg = "\n" + label + ": ";
        if (fieldValue != null) {
            msg += fieldValue;
        }
        return msg;
    }



	/**
     * Loads jira priorities.
     * @return priorities from server.
	 * @throws ConnectorException 
     */
    Priorities loadJiraPriorities() throws ValidationException, ConnectorException {
        if (!getJiraConfig().getServerInfo().isHostSet()) {
            throw new ValidationException("Host URL is not set");
        }
		return JiraLoaders.loadPriorities(getJiraConfig()
				.getServerInfo());
	}

	private CustomFieldsTablePanel createCustomOtherFieldsPanel() {
        CustomFieldsTablePanel customFieldsTablePanel = new CustomFieldsTablePanel(getJiraConfig().getCustomFields());
        return customFieldsTablePanel;
    }

    /**
     * To be used in child panel
     * @return pure config instance
     */
    public JiraConfig getJiraConfig() {
        return (JiraConfig) config;
    }
}
