package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

public class GithubEditorFactory implements PluginEditorFactory {
    /**
     * Bundle name.
     */
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public Descriptor getDescriptor() {
        return GithubDescriptor.instance;
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
}
