package com.taskadapter.connector.redmine.editor;

import com.taskadapter.common.ui.FieldMapping;
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
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelWithPasswordAndAPIKey;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import scala.Option;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public SavableComponent getMiniPanelContents(Sandbox sandbox, RedmineConfig config, WebConnectorSetup setup) {
        Binder<RedmineConfig> binder = new Binder<>(RedmineConfig.class);
        ProjectPanel projectPanel = new ProjectPanel(
                binder,
                "projectKey",
                Option.apply("queryId"),
                Option.empty(),
                new RedmineProjectListLoader(setup),
                new RedmineProjectLoader(config, setup),
                new RedmineQueryListLoader(config, setup),
                this);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 1),
                new FormLayout.ResponsiveStep("50em", 2));

        // TODO 14 priorities panel is read-only for now. its values are not saved
        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                new PrioritiesLoader(setup), this);

        layout.add(projectPanel,
                priorityPanel,
                new OtherRedmineFieldsContainer(binder, config, setup, this));

        binder.readBean(config);
        return new DefaultSavableComponent(layout, () -> {
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
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandboxUnused, WebConnectorSetup setup) {
        return new ServerPanelWithPasswordAndAPIKey(RedmineConnector.ID,
                RedmineConnector.ID, setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return WebConnectorSetup.apply(RedmineConnector.ID, "My Redmine", "", "",
                "", true, "");
    }

    @Override
    public List<BadConfigException> validateForSave(RedmineConfig config, WebConnectorSetup setup,
                                                    List<FieldMapping<?>> fieldMappings) {
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            return Arrays.asList(new ProjectNotSetException());
        }
        return Collections.emptyList();
    }

    @Override
    public List<BadConfigException> validateForLoad(RedmineConfig config, WebConnectorSetup setup) {
        return Collections.emptyList();
    }

    @Override
    public String describeSourceLocation(RedmineConfig config, WebConnectorSetup setup) {
        return setup.getHost();
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
    public void validateForDropInLoad(RedmineConfig config) throws DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }
}
