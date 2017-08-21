package com.taskadapter.connector.github.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.LoginNameNotSpecifiedException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.connector.github.GithubConnector;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelFactory;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;
import scala.Option;

import static com.vaadin.server.Sizeable.Unit.PIXELS;

public class GithubEditorFactory implements PluginEditorFactory<GithubConfig, WebConnectorSetup> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public boolean isWebConnector() {
        return true;
    }

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
        if ("saveRelations".equals(connEx.getMessage())) {
            return MESSAGES.get("errors.unsupported.relations");
        }
        return e.getMessage();
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, GithubConfig config, WebConnectorSetup setup) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(380, PIXELS);
        ProjectPanel projectPanel = new ProjectPanel(new MethodProperty<>(config, "projectKey"),
                new MethodProperty<>(config, "queryString"),
                Interfaces.fromMethod(DataProvider.class, GithubLoaders.class, "getProjects", setup)
                , null, null, this);
        projectPanel.setProjectKeyLabel("Repository ID");
        layout.addComponent(projectPanel);
        return layout;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        return ServerPanelFactory.withLoginAndPassword(GithubConnector.ID(), GithubConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup() {
        return new WebConnectorSetup(GithubConnector.ID(), Option.empty(), "My GitHub", "https://github.com",
                "", "", false, "");
    }

    @Override
    public void validateForSave(GithubConfig config, WebConnectorSetup serverInfo) throws BadConfigException {
        if (Strings.isNullOrEmpty(serverInfo.host())) {
            throw new ServerURLNotSetException();
        }

        if (Strings.isNullOrEmpty(serverInfo.userName())) {
            throw new LoginNameNotSpecifiedException();
        }
    }

    @Override
    public void validateForLoad(GithubConfig config, WebConnectorSetup serverInfo) throws BadConfigException {
        if (Strings.isNullOrEmpty(serverInfo.host())) {
            throw new ServerURLNotSetException();
        }
    }

    @Override
    public String describeSourceLocation(GithubConfig config, WebConnectorSetup setup) {
        return setup.host();
    }

    @Override
    public String describeDestinationLocation(GithubConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public WebConnectorSetup updateForSave(GithubConfig config, Sandbox sandbox, WebConnectorSetup setup)
            throws BadConfigException {
        validateForSave(config, setup);
        return setup;
    }

    @Override
    public void validateForDropInLoad(GithubConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }
}
