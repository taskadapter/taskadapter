package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.mantis.MantisDescriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;

/**
 * @author Alexey Skorokhodov
 */
public class MantisEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return MantisDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, SettingsManager settingsManagerIGNORE) {
        return new MantisEditor(config);
    }

}
