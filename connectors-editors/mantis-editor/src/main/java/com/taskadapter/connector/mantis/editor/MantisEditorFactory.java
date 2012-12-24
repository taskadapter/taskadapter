package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

public class MantisEditorFactory implements PluginEditorFactory<MantisConfig> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof ServerURLNotSetException) {
            return MESSAGES.get("error.serverUrlNotSet");
        }
        if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage())) {
                return MESSAGES.get("error.unsupported.remoteId");
            } else if ("saveRelations".equals(uop.getMessage())) {
                return MESSAGES.get("error.unsupported.relations");
            }
        }
        return null;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, MantisConfig config) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(380, Sizeable.UNITS_PIXELS);
        final WebServerInfo serverInfo = config.getServerInfo();

        ServerPanel serverPanel = new ServerPanel(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));
        layout.addComponent(serverPanel);

        DataProvider<List<? extends NamedKeyedObject>> NULL_QUERY_PROVIDER = null;
        SimpleCallback NULL_PROJECT_INFO_CALLBACK = null;

        layout.addComponent(new ProjectPanel(windowProvider, EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                EditorUtil.wrapNulls(new MethodProperty<String>(config, "queryId")),
                Interfaces.fromMethod(DataProvider.class, MantisLoaders.class,
                        "getProjects", config.getServerInfo())
                , NULL_PROJECT_INFO_CALLBACK, Interfaces.fromMethod(DataProvider.class, MantisLoaders.class, "getFilters", config), this));
        layout.addComponent(new OtherMantisFieldsPanel(config));

        return layout;
    }

    @Override
    public void validateForSave(MantisConfig config) throws BadConfigException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }

        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(MantisConfig config) throws ServerURLNotSetException {
        final WebServerInfo serverInfo = config.getServerInfo();
        if (!serverInfo.isHostSet()) {
            throw new ServerURLNotSetException();
        }
    }

    @Override
    public String describeSourceLocation(MantisConfig config) {
        return config.getServerInfo().getHost();
    }

    @Override
    public String describeDestinationLocation(MantisConfig config) {
        return describeSourceLocation(config);
    }

}
