package com.taskadapter.webui.service;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.LegacyConnectorsSupport;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.PluginEditorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditorManager {
    private static final String COMMENT_SYMBOL = "#";
    private final Map<String, PluginEditorFactory<?>> factories;

    public EditorManager(Map<String, PluginEditorFactory<?>> factories) {
        this.factories = factories;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends ConnectorConfig> PluginEditorFactory<T> getEditorFactory(String connectorId) {
        final String realId = LegacyConnectorsSupport.getRealId(connectorId);
        return (PluginEditorFactory<T>) factories.get(realId);
    }
    
    private static Map<String, PluginEditorFactory<?>> createEditors(Map<String, String> factories) {
        final Map<String, PluginEditorFactory<?>> res = new HashMap<>();
        for (Map.Entry<String, String> spec : factories.entrySet()) {
            try {
                @SuppressWarnings("unchecked")
                final Class<PluginEditorFactory<?>> factoryClass = (Class<PluginEditorFactory<?>>) Class
                        .forName(spec.getValue());
                res.put(spec.getKey(), factoryClass.newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error loading editor class for connector " + spec.getKey() + ". " + e.toString(), e);
            }
        }
        return res;
    }
    
    public static EditorManager fromResource(String resourceName) {
        try {
            String fileContents = Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
            return fromConfig(fileContents);
        } catch (IOException e) {
            throw new RuntimeException("can't load " + resourceName + " from classpath." + e.toString());
        }
    }

    public static EditorManager fromConfig(String config) {
        final String[] lines = config.split("\r\n|\n\r|\n");
        final Map<String, String> implementations = new HashMap<>();
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith(COMMENT_SYMBOL)) {
                continue;
            }
            final String[] parts = line.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Bad config line " + line);
            }
            if (implementations.put(parts[0], parts[1]) != null) {
                throw new IllegalArgumentException(
                        "Duplicate implementation declaration for " + parts[0]);
            }
        }
        return fromImplementationMap(implementations);
    }
    
    public static EditorManager fromImplementationMap(Map<String, String> implementations) {
        return new EditorManager(createEditors(implementations));
    }
    
}
