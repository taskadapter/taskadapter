package com.taskadapter.web.uiapi;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.web.PluginEditorFactory;

/**
 * Implementation of RichConfig. Hides implementation details inside and keeps a
 * config-type magic inside.
 * 
 * @param <T>
 *            type of a connector config.
 */
final class UIConnectorConfigImpl<T extends ConnectorConfig> extends
        UIConnectorConfig {
    private final PluginFactory<T> connectorFactory;
    private final PluginEditorFactory<T> factory;
    private final T config;
    private final String connectorTypeId;

    public UIConnectorConfigImpl(PluginFactory<T> connectorFactory,
            PluginEditorFactory<T> editorFactory, T config,
            String connectorTypeId) {
        this.connectorFactory = connectorFactory;
        this.factory = editorFactory;
        this.config = config;
        this.connectorTypeId = connectorTypeId;
    }

    @Override
    public String getConnectorTypeId() {
        return connectorTypeId;
    }

}
