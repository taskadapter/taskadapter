package com.taskadapter.connector.jira;

import java.util.List;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.model.GProject;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
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
        createServerAndProjectPanelOnTopDefault(new DataProvider<List<? extends NamedKeyedObject>>() {
			@Override
			public List<? extends NamedKeyedObject> loadData()
					throws ValidationException {
				return JiraLoaders.loadProjects(getJiraConfig().getServerInfo());
			}
		}, new SimpleCallback() {
			@Override
			public void callBack() throws ValidationException {
				showProjectInfo();
			}
		}, new DataProvider<List<? extends NamedKeyedObject>>() {
			@Override
			public List<? extends NamedKeyedObject> loadData()
					throws ValidationException {
				return loadQueries();
			}
		});

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
     */
    void showProjectInfo() throws ValidationException {
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
