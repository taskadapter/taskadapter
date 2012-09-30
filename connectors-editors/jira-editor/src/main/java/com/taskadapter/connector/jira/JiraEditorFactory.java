package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.jira.exceptions.BadHostException;
import com.taskadapter.connector.jira.exceptions.BadURIException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;

public class JiraEditorFactory implements PluginEditorFactory {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.github.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return JiraConnector.ID;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new JiraEditor(config, services);
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof BadHostException) {
            return MESSAGES.format("errors.unsupported.illegalHostName", e
                    .getCause().toString());
        } else if (e instanceof UnsupportedOperationException) {
            final UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("updateRemoteIDs".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.remoteId");
            else if ("saveRelations".equals(uop.getMessage()))
                return MESSAGES.get("errors.unsupported.relations");
        } else if (e instanceof BadURIException) {
            return MESSAGES.get("errors.badURI");
        }
        return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return JiraSupportedFields.SUPPORTED_FIELDS;
    }

}
