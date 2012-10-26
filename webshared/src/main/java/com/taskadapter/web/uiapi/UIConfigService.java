package com.taskadapter.web.uiapi;

import com.google.gson.JsonParser;
import com.taskadapter.PluginManager;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.EditorManager;

/**
 * Service to deal with UI connector configurations. Provides canonical and
 * simple ways to create new UI connectors.
 */
public final class UIConfigService {
    private final PluginManager pluginManager;
    private final EditorManager editorManager;

    public UIConfigService(PluginManager pluginManager,
                           EditorManager editorManager) {
        this.pluginManager = pluginManager;
        this.editorManager = editorManager;
    }

    /**
     * Creates a new UI configuration.
     *
     * @param connectorTypeId  connector type ID.
     * @param serializedConfig serialized connector config.
     * @return new UI connector configuration.
     */
    public <T extends ConnectorConfig> UIConnectorConfig createRichConfig(
            String connectorTypeId, String serializedConfig) {
        final PluginFactory<T> connectorFactory = pluginManager
                .getPluginFactory(connectorTypeId);
        final PluginEditorFactory<T> editorFactory = editorManager
                .getEditorFactory(connectorTypeId);
        final T config = connectorFactory.readConfig(new JsonParser()
                .parse(serializedConfig));
        return new UIConnectorConfigImpl<T>(connectorFactory, editorFactory,
                config, connectorTypeId);
    }

    /**
     * Creates a new UI connector configuration.
     *
     * @param connectorTypeId connector type identifier.
     * @return new connector config with default settings.
     */
    public <T extends ConnectorConfig> UIConnectorConfig createDefaultConfig(String connectorTypeId) {
        final PluginFactory<T> connectorFactory = pluginManager.getPluginFactory(connectorTypeId);
        if (connectorFactory == null) {
            throw new RuntimeException("Connector with ID " + connectorTypeId + " not found.");
        }
        final PluginEditorFactory<T> editorFactory = editorManager.getEditorFactory(connectorTypeId);
        final T config = connectorFactory.createDefaultConfig();
        return new UIConnectorConfigImpl<T>(connectorFactory, editorFactory, config, connectorTypeId);
    }
}
