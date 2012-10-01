package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.redmine.RelationCreationException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;

public class RedmineEditorFactory implements PluginEditorFactory {
    /**
     * Bundle name.
     */
    private static final String BUNDLE_NAME = "com.taskadapter.connector.redmine.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return RedmineConnector.ID;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new RedmineEditor(config, services);
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
}
