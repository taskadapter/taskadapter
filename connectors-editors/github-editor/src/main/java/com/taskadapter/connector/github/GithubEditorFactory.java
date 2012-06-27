package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

public class GithubEditorFactory implements PluginEditorFactory {

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
            return "Remote ID storage is not supported";
        else if ("s".equals(connEx.getMessage()))
            return "Issue relations are not supported";
        else
            return null;
    }
}
