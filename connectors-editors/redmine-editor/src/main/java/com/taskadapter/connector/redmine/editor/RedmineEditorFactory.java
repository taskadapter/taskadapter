package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.redmine.RelationCreationException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

public class RedmineEditorFactory implements PluginEditorFactory {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.redmine.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return RedmineConnector.ID;
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof RelationCreationException) {
            return MESSAGES.format("errors.relationsUpdateFailure", e
                    .getCause().getMessage());
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.remoteId");
        }
        return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return RedmineSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, ConnectorConfig someConfig) {
        RedmineConfig config = (RedmineConfig) someConfig;
        RedmineServerPanel redmineServerPanel = new RedmineServerPanel(windowProvider, config);
        ShowProjectElement showProjectElement = new ShowProjectElement(windowProvider, config);
        LoadQueriesElement loadQueriesElement = new LoadQueriesElement(windowProvider, config);
        ProjectPanel projectPanel = new ProjectPanel(windowProvider,
                EditorUtil.wrapNulls(new MethodProperty<String>(someConfig, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<Integer>(someConfig, "queryId")),
                Interfaces.fromMethod(DataProvider.class, RedmineLoaders.class, "getProjects", config.getServerInfo()),
                Interfaces.fromMethod(SimpleCallback.class, showProjectElement, "showProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, loadQueriesElement, "loadQueries"));
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(redmineServerPanel);
        layout.addComponent(projectPanel);
        layout.addComponent(new OtherRedmineFieldsContainer(config));
        return layout;
    }
}