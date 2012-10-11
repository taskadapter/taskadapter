package com.taskadapter.web.uiapi;

import com.google.gson.JsonParser;
import com.taskadapter.PluginManager;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.EditorManager;

/**
 * Service to deal with rich connector configurations. It's just a closure for a
 * required configuration services (execution context).
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
     * @param connectorTypeId
     *            connector type ID.
     * @param serializedConfig
     *            serialized connector config.
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
}
