package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.redmine.RelationCreationException;
import com.taskadapter.redmineapi.RedmineAuthenticationException;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelFactory;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

public class RedmineEditorFactory implements PluginEditorFactory<RedmineConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.redmine.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof RelationCreationException) {
            return MESSAGES.format("errors.relationsUpdateFailure", e
                    .getCause().getMessage());
        } else if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("error.serverUrlNotSet");
        } else if (e instanceof ProjectNotSetException) {
            return MESSAGES.get("error.projectKeyNotSet");
        } else if (e instanceof CommunicationException) {
            if (e.getCause() instanceof RedmineAuthenticationException) {
                return MESSAGES.get("error.authError");
            }
            return MESSAGES.format("error.transportError", e.toString());
        }
        return e.getMessage();
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, RedmineConfig config, WebServerInfo webServerInfo) {
        ShowProjectElement showProjectElement = new ShowProjectElement(config, webServerInfo);
        LoadQueriesElement loadQueriesElement = new LoadQueriesElement(config, webServerInfo);
        ProjectPanel projectPanel = new ProjectPanel(
                new MethodProperty<>(config, "projectKey"),
                new MethodProperty<>(config, "queryIdStr"),
                Interfaces.fromMethod(DataProvider.class, RedmineLoaders.class, "getProjects", webServerInfo),
                Interfaces.fromMethod(SimpleCallback.class, showProjectElement, "showProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, loadQueriesElement, "loadQueries"), this);
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(2);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(projectPanel);
        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(webServerInfo), "loadPriorities"), this);
        gridLayout.addComponent(priorityPanel);
        gridLayout.addComponent(new OtherRedmineFieldsContainer(config, webServerInfo, this));
        return gridLayout;
    }

    @Override
    public ConnectorSetupPanel getSetupPanel(WebServerInfo webServerInfo) {
        return ServerPanelFactory.withApiKeyAndLoginPassword(RedmineConnector.ID(), webServerInfo);
    }

    @Override
    public void validateForSave(RedmineConfig config, WebServerInfo serverInfo) throws BadConfigException {
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(RedmineConfig config,  WebServerInfo serverInfo) {
        // TODO !! Implement
    }

    @Override
    public String describeSourceLocation(RedmineConfig config,  WebServerInfo serverInfo) {
        return serverInfo.getHost();
    }

    @Override
    public String describeDestinationLocation(RedmineConfig config, WebServerInfo serverInfo) {
        return describeSourceLocation(config, serverInfo);
    }

    @Override
    public boolean updateForSave(RedmineConfig config, Sandbox sandbox,  WebServerInfo serverInfo)
            throws BadConfigException {
        validateForSave(config, serverInfo);
        return false;
    }

    @Override
    public void validateForDropInLoad(RedmineConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }
}
