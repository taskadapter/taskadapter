package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.connector.jira.exceptions.BadURIException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.*;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

public class JiraEditorFactory implements PluginEditorFactory<JiraConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.jira.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return JiraConnector.ID;
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof BadHostException) {
            return MESSAGES.format("errors.unsupported.illegalHostName", e
                    .getCause().toString());
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.remoteId");
            else if ("saveRelations".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.relations");
        } else if (e instanceof BadURIException) {
            return MESSAGES.get("errors.badURI");
        }
        return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return JiraSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, JiraConfig config) {
        WebServerInfo serverInfo = config.getServerInfo();
        ServerContainer serverPanel = new ServerContainer(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));

        ShowProjectElement showProjectElement = new ShowProjectElement(windowProvider, config);
        ProjectPanel projectPanel = new ProjectPanel(windowProvider,
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<Integer>(config, "queryId")),
                Interfaces.fromMethod(DataProvider.class, JiraLoaders.class,
                        "loadProjects", config.getServerInfo()),
                Interfaces.fromMethod(SimpleCallback.class, showProjectElement, "loadProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, new LoadQueriesElement(config), "loadQueries"));

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(serverPanel);
        layout.addComponent(projectPanel);

        layout.addComponent(new OtherJiraFieldsPanel(windowProvider, config));

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(config), "loadJiraPriorities"));
        layout.addComponent(priorityPanel);
        layout.addComponent(createCustomOtherFieldsPanel(config));
        return layout;
    }

    private CustomFieldsTablePanel createCustomOtherFieldsPanel(JiraConfig config) {
        CustomFieldsTablePanel customFieldsTablePanel = new CustomFieldsTablePanel(config.getCustomFields());
        return customFieldsTablePanel;
    }

    @Override
    public void validateForSave(JiraConfig config) throws ValidationException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }

        if (config.getProjectKey().isEmpty()) {
            throw new ValidationException("Please specify the Jira project name\n" +
                    "where you want your tasks to be created.");
        }
    }

    @Override
    public void validateForLoad(JiraConfig config) throws ValidationException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }

        if (config.getQueryId() == null) {
            throw new ValidationException("The current Task Adapter version supports loading data from Jira\n" +
                    "only using saved \"Query ID\".\n" +
                    "Please specify it in the Jira configuration dialog");
        }
    }

    @Override
    public String describeSourceLocation(JiraConfig config) {
        return config.getServerInfo().getHost();
    }

    @Override
    public String describeDestinationLocation(JiraConfig config) {
        return describeSourceLocation(config);
    }

}
