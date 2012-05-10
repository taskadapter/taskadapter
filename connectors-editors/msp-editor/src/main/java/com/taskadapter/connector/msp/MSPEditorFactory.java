package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ConfigEditor;
import com.taskadapter.web.service.Services;

/*
 * @author Alexey Skorokhodov
 */
public class MSPEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return MSPDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, Services services) {
        return new MSPEditor(config, services);
    }
}
