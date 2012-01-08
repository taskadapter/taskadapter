package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;

/*
 * @author Alexey Skorokhodov
 */
public class MSPEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return MSPDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, SettingsManager settingsManager) {
        return new MSPEditor(config, settingsManager);
    }
}
