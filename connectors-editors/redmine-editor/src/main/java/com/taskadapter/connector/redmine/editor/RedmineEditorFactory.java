package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RelationCreationException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.PriorityPanel;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelWithAPIKey;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

public class RedmineEditorFactory implements PluginEditorFactory<RedmineConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.redmine.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof RelationCreationException) {
            return MESSAGES.format("errors.relationsUpdateFailure", e
                    .getCause().getMessage());
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.remoteId");
        } else if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("error.serverUrlNotSet");
        }
        return e.getMessage();
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, RedmineConfig config) {
        Panel panel = new Panel("Server Info");
        WebServerInfo serverInfo = config.getServerInfo();
        ServerPanelWithAPIKey redmineServerPanel = new ServerPanelWithAPIKey(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"),
                new MethodProperty<String>(serverInfo, "apiKey"),
                new MethodProperty<Boolean>(serverInfo, "useAPIKeyInsteadOfLoginPassword"));
        panel.addComponent(redmineServerPanel);

        ShowProjectElement showProjectElement = new ShowProjectElement(windowProvider, config);
        LoadQueriesElement loadQueriesElement = new LoadQueriesElement(windowProvider, config);
        ProjectPanel projectPanel = new ProjectPanel(windowProvider,
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<Integer>(config, "queryId")),
                Interfaces.fromMethod(DataProvider.class, RedmineLoaders.class, "getProjects", serverInfo),
                Interfaces.fromMethod(SimpleCallback.class, showProjectElement, "showProjectInfo"),
                Interfaces.fromMethod(DataProvider.class, loadQueriesElement, "loadQueries"), this);
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(2);
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);

        gridLayout.addComponent(panel);
        gridLayout.addComponent(projectPanel);
        PriorityPanel priorityPanel = new PriorityPanel(config.getPriorities(),
                Interfaces.fromMethod(DataProvider.class, new PrioritiesLoader(config), "loadPriorities"), this);
        gridLayout.addComponent(priorityPanel);
        gridLayout.addComponent(new OtherRedmineFieldsContainer(windowProvider, config, this));
        return gridLayout;
    }

    @Override
    public void validateForSave(RedmineConfig config) {
        // TODO !! Implement
    }

    @Override
    public void validateForLoad(RedmineConfig config) {
        // TODO !! Implement
    }

    @Override
    public String describeSourceLocation(RedmineConfig config) {
        return config.getServerInfo().getHost();
    }

    @Override
    public String describeDestinationLocation(RedmineConfig config) {
        return describeSourceLocation(config);
    }
}
