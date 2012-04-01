package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.configeditor.ConfigEditor;

public class GithubEditorFactory implements PluginEditorFactory {
    @Override
    public Descriptor getDescriptor() {
        return GithubDescriptor.instance;
    }

    @Override
    public ConfigEditor createEditor(ConnectorConfig config, SettingsManager settingsManager) {
        return new GithubEditor(config);
    }
}
