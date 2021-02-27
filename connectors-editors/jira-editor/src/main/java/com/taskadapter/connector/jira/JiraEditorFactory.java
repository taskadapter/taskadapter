package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exception.FilterNotSetException;
import com.taskadapter.connector.definition.exception.ForbiddenException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.connector.jira.exceptions.BadURIException;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

public class JiraEditorFactory implements PluginEditorFactory<JiraConfig, WebConnectorSetup> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.jira.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof BadHostException) {
            return MESSAGES.format("errors.unsupported.illegalHostName", e
                    .getCause().toString());
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("saveRelations".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.relations");
        } else if (e instanceof BadURIException) {
            return MESSAGES.get("errors.badURI");
        } else if (e instanceof ProjectNotSetException) {
            return MESSAGES.get("errors.projectKeyNotSet");
        } else if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("errors.serverUrlNotSet");
        } else if (e instanceof FilterNotSetException) {
            return MESSAGES.get("jira.error.filterNotSet");
        } else if (e instanceof NotAuthorizedException) {
            return MESSAGES.get("errors.notAuthorized");
        } else if (e instanceof ForbiddenException) {
            return MESSAGES.get("errors.forbidden");
        } else if (e instanceof DefaultTaskTypeNotSetException) {
            return MESSAGES.get("error.taskTypeNotSet");
        } else if (e instanceof DefaultSubTaskTypeNotSetException) {
            return MESSAGES.get("error.subtasksTypeNotSet");
        }
        return e.toString();
    }

    @Override
    public SavableComponent getMiniPanelContents(Sandbox sandbox, JiraConfig config, WebConnectorSetup setup) {
        Binder<JiraConfig> binder = new Binder<>(JiraConfig.class);

        ProjectPanel projectPanel = new ProjectPanel(
                binder,
                "projectKey",
                Option.apply("queryId"),
                Option.empty(),
                new JiraProjectsListLoader(setup),
                new JiraProjectLoader(config, setup),
                new JiraQueryListLoader(config, setup),
                this);

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 1),
                new FormLayout.ResponsiveStep("50em", 2));

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                new PrioritiesLoader(setup), this);

        layout.add(projectPanel,
                priorityPanel,
                new OtherJiraFieldsPanel(binder, config, setup, this));
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
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        String description = "Note that Jira 7 or older requires a regular user password, while Jira 8+ and Jira Cloud only accept API Tokens."
                + "<br/>Please provide either a password or an API token."
                + "<br/>You can generate an API token here: <br/>" +
                "<b>https://id.atlassian.com/manage-profile/security/api-tokens</b>";
        return new ServerPanel(JiraConnector.ID(), JiraConnector.ID(),
                setup)
                .setPasswordFieldLabel("Password or token")
                .setPasswordHelp(description);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return WebConnectorSetup.apply(JiraConnector.ID(), "My Jira", "https://", "",
                "", true, "");
    }

    @Override
    public List<BadConfigException> validateForSave(JiraConfig config, WebConnectorSetup serverInfo,
                                                    List<FieldMapping<?>> fieldMappings) {
        List<BadConfigException> list = new ArrayList<>();
        if (Strings.isNullOrEmpty(serverInfo.getHost())) {
            list.add(new ServerURLNotSetException());
        }
        if (Strings.isNullOrEmpty(config.getProjectKey())) {
            list.add(new ProjectNotSetException());
        }
        if (Strings.isNullOrEmpty(config.getDefaultTaskType())) {
            list.add(new DefaultTaskTypeNotSetException());
        }
        if (Strings.isNullOrEmpty(config.getDefaultIssueTypeForSubtasks())) {
            list.add(new DefaultSubTaskTypeNotSetException());
        }
        return list;
    }

    @Override
    public List<BadConfigException> validateForLoad(JiraConfig config, WebConnectorSetup serverInfo) {
        List<BadConfigException> list = new ArrayList<>();

        if (Strings.isNullOrEmpty(serverInfo.getHost())) {
            list.add(new ServerURLNotSetException());
        }

        if (config.getQueryId() == null) {
            list.add(new FilterNotSetException());
        }

        return list;
    }

    @Override
    public String describeSourceLocation(JiraConfig config, WebConnectorSetup serverInfo) {
        return serverInfo.getHost();
    }

    @Override
    public String describeDestinationLocation(JiraConfig config, WebConnectorSetup serverInfo) {
        return describeSourceLocation(config, serverInfo);
    }

    @Override
    public Messages fieldNames() {
        return MESSAGES;
    }

    @Override
    public void validateForDropInLoad(JiraConfig config) throws DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

}
