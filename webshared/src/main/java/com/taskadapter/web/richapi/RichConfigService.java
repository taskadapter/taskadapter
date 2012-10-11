package com.taskadapter.web.richapi;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.EditorManager;

/**
 * Service to deal with rich connector configurations. It's just a closure for a
 * required configuration services (execution context).
 */
public final class RichConfigService {
    private final PluginManager pluginManager;
    private final EditorManager editorManager;

    public RichConfigService(PluginManager pluginManager,
            EditorManager editorManager) {
        this.pluginManager = pluginManager;
        this.editorManager = editorManager;
    }

    /**
     * Creates a rich API for a passed data holder. Rich config provides a
     * canonical (and simple) api for working with connector (editing config,
     * accessing connector, localizing error messages, etc...).
     * 
     * @param dataHolder
     *            data holder specifying a new connector.
     */
    public <T extends ConnectorConfig> RichConnectorConfig createRichConfig(
            ConnectorDataHolder<T> dataHolder) {
        final PluginFactory<T> connectorFactory = pluginManager
                .getPluginFactory(dataHolder.getType());
        final PluginEditorFactory<T> editorFactory = editorManager
                .getEditorFactory(dataHolder.getType());
        final T config = dataHolder.getData();
        return new RichConnectorConfigImpl<T>(connectorFactory, editorFactory,
                config, dataHolder.getType());
    }
}
