package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;

/**
 * @author Alexey Skorokhodov
 */
public class JiraEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return JiraDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, SettingsManager settingsManagerIGNORE) {
        return new JiraEditor(config);
    }

}
