package com.taskadapter.web;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.configeditor.ConfigEditor;

/**
 * @author Alexey Skorokhodov
 */
public interface PluginEditorFactory {
    Descriptor getDescriptor();

    ConfigEditor createEditor(ConnectorConfig config, SettingsManager settingsManager);
}
