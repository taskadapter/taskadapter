package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.mantis.MantisDescriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;

public class MantisEditorFactory implements PluginEditorFactory {
    /**
     * Bundle name.
     */
    private static final String BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages";

    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);
    
    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new MantisEditor(config, services);
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
        return null;    }

    @Override
    public AvailableFields getAvailableFields() {
        return MantisSupportedFields.SUPPORTED_FIELDS;
    }

}
