package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class RedmineEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new RedmineEditor(config, services);
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof RelationCreationException) {
            return "Failed to update issue relations : "
                    + e.getCause().getMessage();
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return "Remoted IDs are not supported by Redmine";
        }
        return null;
    }
}
