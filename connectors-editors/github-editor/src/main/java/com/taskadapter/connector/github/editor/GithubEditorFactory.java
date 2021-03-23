package com.taskadapter.connector.github.editor;

import com.google.common.base.Strings;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.LoginNameNotSpecifiedException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.connector.github.GithubConnector;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelWithLoginAndToken;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GithubEditorFactory implements PluginEditorFactory<GithubConfig, WebConnectorSetup> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("errors.serverURLNotSet");
        }
        if (e instanceof ProjectNotSetException) {
            return MESSAGES.get("github.errors.projectNotSet");
        }
        if (e instanceof LoginNameNotSpecifiedException) {
            return MESSAGES.get("errors.loginNameNotSet");
        }
        if (!(e instanceof UnsupportedConnectorOperation)) {
            return e.getMessage();
        }
        var connEx = (UnsupportedConnectorOperation) e;
        if ("saveRelations".equals(connEx.getMessage())) {
            return MESSAGES.get("errors.unsupported.relations");
        }
        return e.getMessage();
    }

    @Override
    public SavableComponent getMiniPanelContents(Sandbox sandbox, GithubConfig config, WebConnectorSetup setup) {
        var binder = new Binder<>(GithubConfig.class);

        var projectPanel = new ProjectPanel(binder,
                "projectKey",
                Optional.empty(),
                Optional.of("queryString"),
                new GithubProjectsListLoader(setup),
                null, null, this);
        projectPanel.setProjectKeyLabel("Repository ID");

        binder.readBean(config);

        return new DefaultSavableComponent(projectPanel, () -> {
            try {
                binder.writeBean(config);
                return true;
            } catch (ValidationException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        var description = "Please generate an API token here: <br/>" +
                "<b>https://github.com/settings/tokens</b>";
        return new ServerPanelWithLoginAndToken(GithubConnector.ID, GithubConnector.ID, setup, description);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return WebConnectorSetup.apply(
                GithubConnector.ID, "My GitHub", "https://github.com", "", "", false, "");
    }

    @Override
    public List<BadConfigException> validateForSave(GithubConfig config, WebConnectorSetup setup, List<FieldMapping<?>> fieldMappings) {
        var seq = new ArrayList<BadConfigException>();
        if (Strings.isNullOrEmpty(setup.getHost())) {
            seq.add(new ServerURLNotSetException());
        }
        if (Strings.isNullOrEmpty(setup.getUserName())) {
            seq.add(new LoginNameNotSpecifiedException());
        }
        return seq;
    }

    @Override
    public List<BadConfigException> validateForLoad(GithubConfig config, WebConnectorSetup setup) {
        var seq = new ArrayList<BadConfigException>();
        if (Strings.isNullOrEmpty(setup.getHost())) {
            seq.add(new ServerURLNotSetException());
        }
        if (Strings.isNullOrEmpty(config.getProjectKey())) {
            seq.add(new ProjectNotSetException());
        }
        return seq;
    }

    @Override
    public void validateForDropInLoad(GithubConfig config) throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

    @Override
    public String describeSourceLocation(GithubConfig config, WebConnectorSetup setup) {
        return setup.getHost();
    }

    @Override
    public String describeDestinationLocation(GithubConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public Messages fieldNames() {
        return MESSAGES;
    }
}
