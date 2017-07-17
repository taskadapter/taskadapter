package com.taskadapter.connector.jira;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.connector.jira.exceptions.BadURIException;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.CustomFieldsTablePanel;
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

public class JiraEditorFactory implements PluginEditorFactory<JiraConfig> {
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
        } else if (e instanceof NotAuthorizedException) {
            return MESSAGES.get("errors.notAuthorized");
        } else if (e instanceof JiraConfigException) {
            return formatValidationErrors((JiraConfigException) e);
        }
        return e.getMessage();
    }
    
    private static String formatValidationErrors(JiraConfigException exn) {
        final StringBuilder res = new StringBuilder("* ");
        
        final Iterator<JiraValidationErrorKind> itr = exn.getErrors().iterator();
        res.append(getValidationMessage(itr.next()));
        while (itr.hasNext()) {
            res.append("<br/>\n* ").append(getValidationMessage(itr.next()));
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
            return MESSAGES.get("error.queryIdNotSet");
        }
        return "??=>" + kind.toString();
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, JiraConfig config) {
        WebServerInfo serverInfo = config.getServerInfo();
        ServerPanel serverPanel = new ServerPanel(new MethodProperty<>(config, "label"),
                new MethodProperty<>(serverInfo, "host"),
                new MethodProperty<>(serverInfo, "userName"),
                new MethodProperty<>(serverInfo, "password"));

        ShowProjectElement showProjectElement = new ShowProjectElement(config);
        ProjectPanel projectPanel = new ProjectPanel(
                new MethodProperty<>(config, "projectKey"),
                new MethodProperty<>(config, "queryIdStr"),
                Interfaces.fromMethod(DataProvider.class, JiraLoaders.class,
                        "loadProjects", config.getServerInfo()),
                Interfaces.fromMethod(SimpleCallback.class, showProjectElement, "loadProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, new LoadQueriesElement(config), "loadQueries"), this);
        projectPanel.setHeight(100, PERCENTAGE);

        GridLayout gridLayout = new GridLayout(2, 4);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(serverPanel);
        gridLayout.addComponent(projectPanel);

        OtherJiraFieldsPanel otherJiraFieldsPanel = new OtherJiraFieldsPanel(config, this);
        otherJiraFieldsPanel.setHeight(100, PERCENTAGE);
        gridLayout.addComponent(otherJiraFieldsPanel);

        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(config), "loadJiraPriorities"), this);
        gridLayout.addComponent(priorityPanel);
        gridLayout.addComponent(createCustomOtherFieldsPanel(config));
        return gridLayout;
    }

    private CustomFieldsTablePanel createCustomOtherFieldsPanel(JiraConfig config) {
        return new CustomFieldsTablePanel(config.getCustomFields());
    }

    @Override
    public void validateForSave(JiraConfig config) throws BadConfigException {
        final Collection<JiraValidationErrorKind> errors = new LinkedHashSet<>();
        
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
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
    public void validateForLoad(JiraConfig config) throws BadConfigException {
        final Collection<JiraValidationErrorKind> errors = new LinkedHashSet<>();
        
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            errors.add(JiraValidationErrorKind.HOST_NOT_SET);
        }

        if (config.getQueryId() == null) {
            errors.add(JiraValidationErrorKind.QUERY_ID_NOT_SET);
        }
        
        if (!errors.isEmpty())
            throw new JiraConfigException(errors);
    }

    @Override
    public String describeSourceLocation(JiraConfig config) {
        return config.getServerInfo().getHost();
    }

    @Override
    public String describeDestinationLocation(JiraConfig config) {
        return describeSourceLocation(config);
    }

    @Override
    public boolean updateForSave(JiraConfig config, Sandbox sandbox)
            throws BadConfigException {
        validateForSave(config);
        return false;
    }

    @Override
    public void validateForDropInLoad(JiraConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

}
