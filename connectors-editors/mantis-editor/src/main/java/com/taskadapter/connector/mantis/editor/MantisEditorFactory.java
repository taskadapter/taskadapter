package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.mantis.MantisDescriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

/**
 * @author Alexey Skorokhodov
 */
public class MantisEditorFactory implements PluginEditorFactory {
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
                return "Remoted IDs are not supported by Mantis";
            else if ("saveRelations".equals(uop.getMessage()))
                return "Issue relations are not supported by Mantis";
        }
        return null;    }

}
