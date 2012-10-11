package com.taskadapter.config;

/**
 * Stored connector configuration. Just a plain data and nothing else.
 */
public final class StoredConnectorConfig {
    private final String connectorTypeId;
    private String serializedConfig;

    public StoredConnectorConfig(String connectorTypeId,
            String serializedConfig) {
        this.connectorTypeId = connectorTypeId;
        this.serializedConfig = serializedConfig;
    }

    public String getSerializedConfig() {
        return serializedConfig;
    }

    public void setSerializedConfig(String serializedConfig) {
        this.serializedConfig = serializedConfig;
    }

    public String getConnectorTypeId() {
        return connectorTypeId;
    }

}
