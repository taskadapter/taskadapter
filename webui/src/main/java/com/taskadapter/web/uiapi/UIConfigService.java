package com.taskadapter.web.uiapi;

import com.google.gson.JsonParser;
import com.taskadapter.PluginManager;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.webui.service.EditorManager;

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
    public <C extends ConnectorConfig, S extends ConnectorSetup> UIConnectorConfig createRichConfig(
            String connectorTypeId, String serializedConfig) {
        final PluginFactory<C, S> connectorFactory = pluginManager.getPluginFactory(connectorTypeId);
        if (connectorFactory == null) {
            throw new RuntimeException("Connector with ID '" + connectorTypeId +
                    "' is unknown. Are you using Task Adapter with very old config files?");
        }
        final PluginEditorFactory<C, S> editorFactory = editorManager.getEditorFactory(connectorTypeId);
        final C config = connectorFactory.readConfig(new JsonParser().parse(serializedConfig));
        return new UIConnectorConfigImpl<>(connectorFactory, editorFactory,
                config, connectorTypeId);
    }

    /**
     * Creates a new UI connector configuration.
     *
     * @param connectorTypeId connector type identifier.
     * @return new connector config with default settings.
     */
    public <C extends ConnectorConfig, S extends ConnectorSetup> UIConnectorConfig createDefaultConfig(String connectorTypeId) {
        final PluginFactory<C, S> connectorFactory = pluginManager.getPluginFactory(connectorTypeId);
        if (connectorFactory == null) {
            throw new RuntimeException("Connector with ID " + connectorTypeId + " not found.");
        }
        final PluginEditorFactory<C, S> editorFactory = editorManager.getEditorFactory(connectorTypeId);
        final C config = connectorFactory.createDefaultConfig();
        return new UIConnectorConfigImpl<>(connectorFactory, editorFactory, config, connectorTypeId);
    }
}
