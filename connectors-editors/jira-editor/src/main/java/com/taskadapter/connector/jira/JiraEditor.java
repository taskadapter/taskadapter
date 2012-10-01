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

    private static final String CONNECTOR_TYPE_LABEL = "Atlassian Jira";

    public JiraEditor(ConnectorConfig config, Services services) {
        super(config, services);
        buildUI();
    }

    @SuppressWarnings("unchecked")
    private void buildUI() {
        MiniServerPanel miniServerPanel = new MiniServerPanel(this, CONNECTOR_TYPE_LABEL, config);
        WebServerInfo serverInfo = ((JiraConfig)config).getServerInfo();
        ServerContainer serverPanel = new ServerContainer(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));

        miniServerPanel.setServerPanel(serverPanel);
        Panel panel = new Panel(miniServerPanel);
        panel.setCaption(CONNECTOR_TYPE_LABEL);
        addToLeftColumn(panel);

        // right column
        addToRightColumn(new ProjectPanel(this,
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<Integer>(config, "queryId")),
                Interfaces.fromMethod(DataProvider.class, JiraLoaders.class,
                        "loadProjects", getJiraConfig().getServerInfo()),
                Interfaces.fromMethod(SimpleCallback.class, this, "loadProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, this, "loadQueries")));

        // left column
        OtherJiraFieldsPanel jiraFieldsPanel = new OtherJiraFieldsPanel(this, getJiraConfig());
        addToLeftColumn(jiraFieldsPanel);

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, this, "loadJiraPriorities"));
        addToLeftColumn(priorityPanel);

        // right column
        addToRightColumn(createCustomOtherFieldsPanel());
        AvailableFields supportedFields = JiraSupportedFields.SUPPORTED_FIELDS;
        addToRightColumn(new FieldsMappingPanel(supportedFields, config.getFieldMappings()));
    }

    List<? extends NamedKeyedObject> loadQueries() {
        try {
            return new JiraConnector(getJiraConfig()).getFilters();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows a project info.
     *
     * @throws ValidationException
     * @throws ConnectorException
     */
    void loadProjectInfo() throws ValidationException, ConnectorException {
        JiraConfig webConfig = (JiraConfig) config;
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
