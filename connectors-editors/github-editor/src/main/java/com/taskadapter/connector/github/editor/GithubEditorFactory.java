package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.connector.github.GithubConnector;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

public class GithubEditorFactory implements PluginEditorFactory<GithubConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return GithubConnector.ID;
    }

    @Override
    public String formatError(Throwable e) {
        if (!(e instanceof UnsupportedConnectorOperation)) {
            return null;
        }

        final UnsupportedConnectorOperation connEx = (UnsupportedConnectorOperation) e;
        if ("updateRemoteIDs".equals(connEx.getMessage()))
            return MESSAGES.get("errors.unsupported.remoteId");
        else if ("saveRelations".equals(connEx.getMessage()))
            return MESSAGES.get("errors.unsupported.relations");
        else
            return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return GithubSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, GithubConfig config) {
        VerticalLayout layout = new VerticalLayout();
        final WebServerInfo serverInfo = config.getServerInfo();
        ServerPanel serverPanel = new ServerPanel(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));
        serverPanel.disableServerURLField();
        layout.addComponent(serverPanel);

        ProjectPanel projectPanel = new ProjectPanel(windowProvider, EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "queryString")),
                Interfaces.fromMethod(DataProvider.class, GithubLoaders.class, "getProjects", serverInfo)
                , null, null);
        projectPanel.setProjectKeyLabel("Repository ID");
        layout.addComponent(projectPanel);
        return layout;
    }

    @Override
    public void validateForSave(GithubConfig config) throws ValidationException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }

        if(serverInfo.getUserName().isEmpty()) {
            throw new ValidationException("User login name is required.");
        }
    }

    @Override
    public void validateForLoad(GithubConfig config) throws ValidationException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ValidationException("Server URL is not set");
        }
    }

    @Override
    public String describeSourceLocation(GithubConfig config) {
        return config.getServerInfo().getHost();
    }

    @Override
    public String describeDestinationLocation(GithubConfig config) {
        return describeSourceLocation(config);
    }
}
