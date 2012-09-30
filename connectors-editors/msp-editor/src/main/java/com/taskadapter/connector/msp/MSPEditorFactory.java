package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;

public class MSPEditorFactory implements PluginEditorFactory {
    /**
     * Bundle name.
     */
    private static final String BUNDLE_NAME = "com.taskadapter.connector.msp.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String getId() {
        return MSPConnector.ID;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new MSPEditor(config, services);
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof UnsupportedRelationType) {
            return MESSAGES.format(
                    "errors.unsupportedRelation",
                    MESSAGES.get("relations."
                            + ((UnsupportedRelationType) e).getRelationType()));
        }
        return null;
    }

    @Override
    public AvailableFields getAvailableFields() {
        return MSPSupportedFields.SUPPORTED_FIELDS;
    }
}
