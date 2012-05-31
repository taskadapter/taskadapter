package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;

public abstract class AbstractConnector<T extends ConnectorConfig> implements Connector<T> {

    protected T config;

    protected AbstractConnector(T config) {
        super();
        this.config = config;
    }

    @Override
    public T getConfig() {
        return config;
    }

}
