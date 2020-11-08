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
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

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
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, JiraConfig config, WebConnectorSetup setup) {
        ProjectPanel projectPanel = new ProjectPanel(
                new MethodProperty<>(config, "projectKey"),
                Option.apply(new MethodProperty<>(config, "queryId")),
                Option.empty(),
                new JiraProjectsListLoader(setup),
                new JiraProjectLoader(config, setup),
                new JiraQueryListLoader(config, setup),
                this);
        projectPanel.setHeight(100, PERCENTAGE);

        GridLayout gridLayout = new GridLayout(2, 4);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(projectPanel);

        OtherJiraFieldsPanel otherJiraFieldsPanel = new OtherJiraFieldsPanel(config, setup, this);
        otherJiraFieldsPanel.setHeight(100, PERCENTAGE);
        gridLayout.addComponent(otherJiraFieldsPanel);

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(setup), "loadJiraPriorities"), this);
        gridLayout.addComponent(priorityPanel);
        return gridLayout;
    }

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        String description = "Please generate an API token here: <br/>" +
                "<b>https://id.atlassian.com/manage-profile/security/api-tokens</b>";
        return ServerPanelFactory.withEmailAndApiToken(JiraConnector.ID(), JiraConnector.ID(),
                description, setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return new WebConnectorSetup(JiraConnector.ID(), Option.empty(), "My Jira", "https://", "",
                "", true, "");
    }

    @Override
    public Seq<BadConfigException> validateForSave(JiraConfig config, WebConnectorSetup serverInfo,
                                                   Seq<FieldMapping<?>> fieldMappings) {
        List<BadConfigException> list = new ArrayList<>();
        if (Strings.isNullOrEmpty(serverInfo.host())) {
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
        return JavaConverters.asScalaBuffer(list);
    }

    @Override
    public Seq<BadConfigException> validateForLoad(JiraConfig config, WebConnectorSetup serverInfo) {
        List<BadConfigException> list = new ArrayList<>();

        if (Strings.isNullOrEmpty(serverInfo.host())) {
            list.add(new ServerURLNotSetException());
        }

        if (config.getQueryId() == null) {
            list.add(new FilterNotSetException());
        }

        return JavaConverters.asScalaBuffer(list);
    }

    @Override
    public String describeSourceLocation(JiraConfig config, WebConnectorSetup serverInfo) {
        return serverInfo.host();
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
