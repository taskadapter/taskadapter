package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisConnector;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.callbacks.SimpleCallback;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.magic.Interfaces;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

import java.util.List;

public class MantisEditorFactory implements PluginEditorFactory {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return MantisConnector.ID;
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.remoteId");
            else if ("saveRelations".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.relations");
        }
        return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return MantisSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, ConnectorConfig config) {
        VerticalLayout layout = new VerticalLayout();

        final WebServerInfo serverInfo = ((MantisConfig) config).getServerInfo();

        ServerPanel serverPanel = new ServerPanel(new MethodProperty<String>(config, "label"),
                new MethodProperty<String>(serverInfo, "host"),
                new MethodProperty<String>(serverInfo, "userName"),
                new MethodProperty<String>(serverInfo, "password"));
        layout.addComponent(serverPanel);

        DataProvider<List<? extends NamedKeyedObject>> NULL_QUERY_PROVIDER = null;
        SimpleCallback NULL_PROJECT_INFO_CALLBACK = null;

        layout.addComponent(new ProjectPanel(windowProvider, EditorUtil.wrapNulls(new MethodProperty<String>(config, "projectKey")),
                null,
                Interfaces.fromMethod(DataProvider.class, MantisLoaders.class,
                        "getProjects", ((MantisConfig) config).getServerInfo())
                , NULL_PROJECT_INFO_CALLBACK, NULL_QUERY_PROVIDER));
        layout.addComponent(new OtherMantisFieldsPanel((MantisConfig) config));

        return layout;
    }

}
