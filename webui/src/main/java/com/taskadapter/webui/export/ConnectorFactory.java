package com.taskadapter.webui.export;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.PluginFactory;

class ConnectorFactory {

    private PluginManager pluginManager;

    ConnectorFactory(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    <T extends ConnectorConfig> Connector<T> getConnector(ConnectorDataHolder connectorDataHolder) {
        final PluginFactory<T> factory = pluginManager.getPluginFactory(connectorDataHolder.getType());
        @SuppressWarnings("unchecked")
        final T config = (T) connectorDataHolder.getData();
        return factory.createConnector(config);
    }

}
