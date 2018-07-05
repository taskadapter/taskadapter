package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.ValidationErrorBuilder;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exception.ConfigValidationError;
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
import scala.collection.Seq;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

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
        } else if (e instanceof JiraConfigException) {
            return formatValidationErrors((JiraConfigException) e);
        }
        return e.getMessage();
    }

    private static String formatValidationErrors(JiraConfigException exn) {
        StringBuilder res = new StringBuilder();

        final Iterator<JiraValidationErrorKind> itr = exn.getErrors().iterator();
        res.append(getValidationMessage(itr.next()));
        while (itr.hasNext()) {
            res.append("<br/>\n").append(getValidationMessage(itr.next()));
        }

        return res.toString();
    }

    private static String getValidationMessage(JiraValidationErrorKind kind) {
        switch (kind) {
            case HOST_NOT_SET:
                return MESSAGES.get("errors.serverUrlNotSet");
            case PROJECT_NOT_SET:
                return MESSAGES.get("errors.projectKeyNotSet");
            case DEFAULT_TASK_TYPE_NOT_SET:
                return MESSAGES.get("error.taskTypeNotSet");
            case DEFAULT_SUBTASK_TYPE_NOT_SET:
                return MESSAGES.get("error.subtasksTypeNotSet");
            case QUERY_ID_NOT_SET:
                return MESSAGES.get("jira.error.filterNotSet");
        }
        return "??=>" + kind.toString();
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

        OtherJiraFieldsPanel otherJiraFieldsPanel = new OtherJiraFieldsPanel(config,setup, this);
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
        return ServerPanelFactory.withLoginAndPassword(JiraConnector.ID(), JiraConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup() {
        return new WebConnectorSetup(JiraConnector.ID(), Option.empty(), "My JIRA", "http://", "",
                "", false, "");
    }

    @Override
    public void validateForSave(JiraConfig config, WebConnectorSetup serverInfo, Seq<FieldMapping<?>> fieldMappings) throws BadConfigException {
        final Collection<JiraValidationErrorKind> errors = new LinkedHashSet<>();

        if (Strings.isNullOrEmpty(serverInfo.host())) {
            errors.add(JiraValidationErrorKind.HOST_NOT_SET);
        }

        if (Strings.isNullOrEmpty(config.getProjectKey())) {
            errors.add(JiraValidationErrorKind.PROJECT_NOT_SET);
        }

        if (Strings.isNullOrEmpty(config.getDefaultTaskType())) {
            errors.add(JiraValidationErrorKind.DEFAULT_TASK_TYPE_NOT_SET);
        }

        if (Strings.isNullOrEmpty(config.getDefaultIssueTypeForSubtasks())) {
            errors.add(JiraValidationErrorKind.DEFAULT_SUBTASK_TYPE_NOT_SET);
        }

        if (!errors.isEmpty())
            throw new JiraConfigException(errors);
    }

    @Override
    public Seq<ConfigValidationError> validateForLoad(JiraConfig config, WebConnectorSetup serverInfo) {
        ValidationErrorBuilder builder = new ValidationErrorBuilder();

        if (Strings.isNullOrEmpty(serverInfo.host())) {
            builder.error(new ServerURLNotSetException());
        }

        if (config.getQueryId() == null) {
            builder.error(new FilterNotSetException());
        }

        return builder.build();
    }

    @Override
    public String describeSourceLocation(JiraConfig config, WebConnectorSetup serverInfo) {
        return serverInfo.host();
    }

    @Override
    public String describeDestinationLocation(JiraConfig config,  WebConnectorSetup serverInfo) {
        return describeSourceLocation(config, serverInfo);
    }

    @Override
    public Messages fieldNames() {
        return MESSAGES;
    }

    @Override
    public WebConnectorSetup updateForSave(JiraConfig config, Sandbox sandbox, WebConnectorSetup setup,
                                           Seq<FieldMapping<?>> fieldMappings)
            throws BadConfigException {
        validateForSave(config, setup, fieldMappings);
        return setup;
    }

    @Override
    public void validateForDropInLoad(JiraConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

}
