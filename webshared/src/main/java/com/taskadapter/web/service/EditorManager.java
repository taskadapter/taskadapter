package com.taskadapter.web.service;

import com.taskadapter.LegacyConnectorsSupport;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.PluginEditorFactory;

import java.util.Map;

public class EditorManager {
    private final Map<String, String> editors;

    public EditorManager(Map<String, String> editors) {
        this.editors = editors;
    }

    @SuppressWarnings("unchecked")
    public <T extends ConnectorConfig> PluginEditorFactory<T> getEditorFactory(String connectorId) {
        String realId = LegacyConnectorsSupport.getRealId(connectorId);
        String className = editors.get(realId);
        try {
            Class<PluginEditorFactory<?>> factoryClass = (Class<PluginEditorFactory<?>>) Class.forName(className);
            return (PluginEditorFactory<T>) factoryClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error loading editor class for connector " + connectorId + ". " + e.toString(), e);
        }
    }

}
