package com.taskadapter.config;

import com.taskadapter.web.uiapi.SetupId;

import java.util.Objects;

/**
 * Stored connector configuration. Just a plain data and nothing else.
 */
public class StoredConnectorConfig {
    private String connectorTypeId;
    private SetupId connectorSavedSetupId;
    private String serializedConfig;

    public StoredConnectorConfig(String connectorTypeId, SetupId connectorSavedSetupId, String serializedConfig) {
        this.connectorTypeId = connectorTypeId;
        this.connectorSavedSetupId = connectorSavedSetupId;
        this.serializedConfig = serializedConfig;
    }

    public String getConnectorTypeId() {
        return connectorTypeId;
    }

    public void setConnectorTypeId(String connectorTypeId) {
        this.connectorTypeId = connectorTypeId;
    }

    public SetupId getConnectorSavedSetupId() {
        return connectorSavedSetupId;
    }

    public void setConnectorSavedSetupId(SetupId connectorSavedSetupId) {
        this.connectorSavedSetupId = connectorSavedSetupId;
    }

    public String getSerializedConfig() {
        return serializedConfig;
    }

    public void setSerializedConfig(String serializedConfig) {
        this.serializedConfig = serializedConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoredConnectorConfig that = (StoredConnectorConfig) o;
        return Objects.equals(connectorTypeId, that.connectorTypeId) && Objects.equals(connectorSavedSetupId, that.connectorSavedSetupId) && Objects.equals(serializedConfig, that.serializedConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorTypeId, connectorSavedSetupId, serializedConfig);
    }

    @Override
    public String toString() {
        return "StoredConnectorConfig{" +
                "connectorTypeId='" + connectorTypeId + '\'' +
                ", connectorSavedSetupId=" + connectorSavedSetupId +
                ", serializedConfig='" + serializedConfig + '\'' +
                '}';
    }
}
