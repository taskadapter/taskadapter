package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.github.GithubConnector;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

public class GithubEditorFactory implements PluginEditorFactory {
    /**
     * Bundle name.
     */
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return GithubConnector.ID;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new GithubEditor(config, services);
    }

    @Override
    public String formatError(Throwable e) {
        if (!(e instanceof UnsupportedConnectorOperation)) {
            return null;
        }

        final UnsupportedConnectorOperation connEx = (UnsupportedConnectorOperation) e;
        if ("updateRemoteIDs".equals(connEx.getMessage()))
            return MESSAGES.get("errors.unsupported.remoteId");
        else if ("saveRelations".equals(connEx.getMessage()))
            return MESSAGES.get("errors.unsupported.relations");
        else
            return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return GithubSupportedFields.SUPPORTED_FIELDS;
    }

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, ConnectorConfig config) {
        // TODO !!!
        return new VerticalLayout();
    }
}
