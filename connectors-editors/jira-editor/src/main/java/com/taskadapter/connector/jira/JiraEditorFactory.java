package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.connector.jira.exceptions.BadURIException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.CustomFieldsTablePanel;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;

public class JiraEditorFactory implements PluginEditorFactory<JiraConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.jira.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

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
        } else if (e instanceof ProjectNotSetException) {
            return MESSAGES.get("errors.projectKeyNotSet");
        } else if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("errors.serverUrlNotSet");
        } else if (e instanceof QueryIdNotSetException) {
            return MESSAGES.get("error.queryIdNotSet");
        }
        return e.getMessage();
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, JiraConfig config) {
        WebServerInfo serverInfo = config.getServerInfo();
        ServerPanel serverPanel = new ServerPanel(new MethodProperty<String>(config, "label"),
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
                Interfaces.fromMethod(DataProvider.class, new LoadQueriesElement(config), "loadQueries"), this);
        projectPanel.setHeight(100, Sizeable.UNITS_PERCENTAGE);

        GridLayout gridLayout = new GridLayout(2, 4);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(serverPanel);
        gridLayout.addComponent(projectPanel);

        OtherJiraFieldsPanel otherJiraFieldsPanel = new OtherJiraFieldsPanel(windowProvider, config, this);
        otherJiraFieldsPanel.setHeight(100, Sizeable.UNITS_PERCENTAGE);
        gridLayout.addComponent(otherJiraFieldsPanel);

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(config), "loadJiraPriorities"));
        gridLayout.addComponent(priorityPanel);
        gridLayout.addComponent(createCustomOtherFieldsPanel(config));
        return gridLayout;
    }

    private CustomFieldsTablePanel createCustomOtherFieldsPanel(JiraConfig config) {
        return new CustomFieldsTablePanel(config.getCustomFields());
    }

    @Override
    public void validateForSave(JiraConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }

        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(JiraConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }

        if (config.getQueryId() == null) {
            throw new QueryIdNotSetException();
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
