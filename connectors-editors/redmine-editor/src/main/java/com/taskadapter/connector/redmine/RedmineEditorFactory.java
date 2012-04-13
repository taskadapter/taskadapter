package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;

/**
 * @author Alexey Skorokhodov
 */
public class RedmineEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return RedmineDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, SettingsManager settingsManagerIGNORE) {
        return new RedmineEditor(config);
    }
}
