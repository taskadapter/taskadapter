package com.taskadapter.config;

/**
 * Stored configuration for a single export.
 * 
 * @author maxkar
 * 
 */
public final class StoredExportConfig {
    /**
     * Export configruation id. It is implementation dependend and at a current
     * time it is an absolute file name.
     */
    private final String id;
    private final String name;
    private final StoredConnectorConfig connector1;
    private final StoredConnectorConfig connector2;
    private final String mappings;

    /**
     * Not public. And shoult not be. It is used only during a loading process
     * and later should be decomposed into mutable "ui" settings.
     */
    StoredExportConfig(String id, String name,
            StoredConnectorConfig connector1, StoredConnectorConfig connector2,
            String mappings) {
        this.id = id;
        this.name = name;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.mappings = mappings;
    }

    /**
     * Returns an unique config id. Format of an ID is implementation-dependent
     * and should not be parsed (ony any other way used) by a client.
     * 
     * @return implementation-specific export configuration id.
     */
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public StoredConnectorConfig getConnector1() {
        return connector1;
    }

    public StoredConnectorConfig getConnector2() {
        return connector2;
    }

    public String getMappings() {
        return mappings;
    }

    @Override
    public String toString() {
        return "StoredExportConfig{" +
                "name='" + name + '\'' +
                '}';
    }
}
