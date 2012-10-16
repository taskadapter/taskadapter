package com.taskadapter.web.service;

import com.taskadapter.LegacyConnectorsSupport;
import com.taskadapter.PluginsFileParser;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.PluginEditorFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EditorManager {
    private Map<String, PluginEditorFactory<?>> editorFactories = new HashMap<String, PluginEditorFactory<?>>();

    public EditorManager() {
        loadEditors();
    }

    private void loadEditors() {
        try {
            Collection<String> classNames = new PluginsFileParser().parseResource("editors.properties");
            for (String factoryClassName : classNames) {
                @SuppressWarnings("unchecked")
                Class<PluginEditorFactory<?>> factoryClass = (Class<PluginEditorFactory<?>>) Class.forName(factoryClassName);
                PluginEditorFactory<?> pluginFactory = factoryClass.newInstance();
                String connectorId = pluginFactory.getId();
                editorFactories.put(connectorId, pluginFactory);
            }
        } catch (Exception e) {
            throw new RuntimeException("Loading editors: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ConnectorConfig> PluginEditorFactory<T> getEditorFactory(String connectorId) {
        String realId = LegacyConnectorsSupport.getRealId(connectorId);
        return (PluginEditorFactory<T>) editorFactories.get(realId);
    }

}
