package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
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
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelFactory;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import scala.Option;
import scala.collection.Seq;

public class RedmineEditorFactory implements PluginEditorFactory<RedmineConfig, WebConnectorSetup> {
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
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, RedmineConfig config, WebConnectorSetup setup) {
        ProjectPanel projectPanel = new ProjectPanel(
                new MethodProperty<>(config, "projectKey"),
                Option.apply(new MethodProperty<>(config, "queryId")),
                Option.empty(),
                new RedmineProjectListLoader(setup),
                new RedmineProjectLoader(config, setup),
                new RedmineQueryListLoader(config, setup),
    this);
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(2);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(projectPanel);
        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(setup), "loadPriorities"), this);
        gridLayout.addComponent(priorityPanel);
        gridLayout.addComponent(new OtherRedmineFieldsContainer(config, setup, this));
        return gridLayout;
    }

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandboxUnused, WebConnectorSetup setup) {
        return ServerPanelFactory.withApiKeyAndLoginPassword(RedmineConnector.ID(),
                RedmineConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup() {
        return new WebConnectorSetup(RedmineConnector.ID(), Option.empty(), "My Redmine", "", "",
                "", true, "");
    }

    @Override
    public void validateForSave(RedmineConfig config, WebConnectorSetup setup, Seq<FieldMapping<?>> fieldMappings) throws BadConfigException {
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(RedmineConfig config, WebConnectorSetup setup) {
    }

    @Override
    public String describeSourceLocation(RedmineConfig config, WebConnectorSetup setup) {
        return setup.host();
    }

    @Override
    public String describeDestinationLocation(RedmineConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public Messages fieldNames() {
        return MESSAGES;
    }

    @Override
    public WebConnectorSetup updateForSave(RedmineConfig config, Sandbox sandbox, WebConnectorSetup setup,
                                           Seq<FieldMapping<?>> fieldMappings)
            throws BadConfigException {
        validateForSave(config, setup, fieldMappings);
        return setup;
    }

    @Override
    public void validateForDropInLoad(RedmineConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }
}
