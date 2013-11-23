package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.LoginNameNotSpecifiedException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class GithubEditorFactory implements PluginEditorFactory<GithubConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {

        if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("errors.serverURLNotSet");
        }
        if (e instanceof LoginNameNotSpecifiedException) {
            return MESSAGES.get("errors.loginNameNotSet");
        }
        if (!(e instanceof UnsupportedConnectorOperation)) {
            return e.getMessage();
        }

        final UnsupportedConnectorOperation connEx = (UnsupportedConnectorOperation) e;
        if ("updateRemoteIDs".equals(connEx.getMessage())) {
            return MESSAGES.get("errors.unsupported.remoteId");
        } else if ("saveRelations".equals(connEx.getMessage())) {
            return MESSAGES.get("errors.unsupported.relations");
        } else {
            return e.getMessage();
        }
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, GithubConfig config) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(380, PIXELS);
        final WebServerInfo serverInfo = config.getServerInfo();
        MethodProperty<String> serverUrlProperty = new MethodProperty<String>(serverInfo, "host");
        serverUrlProperty.setReadOnly(true);
        ServerPanel serverPanel = new ServerPanel(new MethodProperty<String>(config, "label"),
                serverUrlProperty,
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));
        layout.addComponent(serverPanel);

        ProjectPanel projectPanel = new ProjectPanel(EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "queryString")),
                Interfaces.fromMethod(DataProvider.class, GithubLoaders.class, "getProjects", serverInfo)
                , null, null, this);
        projectPanel.setProjectKeyLabel("Repository ID");
        layout.addComponent(projectPanel);
        return layout;
    }

    @Override
    public void validateForSave(GithubConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }

        if (serverInfo.getUserName().isEmpty()) {
            throw new LoginNameNotSpecifiedException();
        }
    }

    @Override
    public void validateForLoad(GithubConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
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

    @Override
    public boolean updateForSave(GithubConfig config, Sandbox sandbox)
            throws BadConfigException {
        validateForSave(config);
        return false;
    }
}
