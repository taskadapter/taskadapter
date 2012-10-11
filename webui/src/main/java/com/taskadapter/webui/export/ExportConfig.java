package com.taskadapter.webui.export;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.MappingBuilder;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;

/**
 * Export specification. Contains data about source and target connectors and
 * configurations.
 * 
 * @param <S>
 *            source connector config type.
 * @param <T>
 *            target connector config type.
 */
public final class ExportConfig<S extends ConnectorConfig, T extends ConnectorConfig> {
    private final ConnectorDataHolder<S> sourceConfig;
    private final ConnectorDataHolder<T> targetConfig;
    private NewMappings mappings;

    private ExportConfig(ConnectorDataHolder<S> sourceConfig,
            ConnectorDataHolder<T> targetConfig, NewMappings mappings) {
        this.sourceConfig = sourceConfig;
        this.targetConfig = targetConfig;
        this.mappings = mappings;
    }

    public ConnectorDataHolder<S> getSourceConfig() {
        return sourceConfig;
    }

    public ConnectorDataHolder<T> getTargetConfig() {
        return targetConfig;
    }

    public NewMappings getMappings() {
        return mappings;
    }

    /**
     * @Deprecated. Should properly create and use UI configs instead. This
     *              config must be deeply unmodifiable.
     */
    @Deprecated
    public void setMappings(NewMappings mappings) {
        this.mappings = mappings;
    }

    public Mappings generateSourceMappings() {
        return MappingBuilder.build(mappings, MappingSide.LEFT);
    }

    public Mappings generateTargetMappings() {
        return MappingBuilder.build(mappings, MappingSide.RIGHT);
    }

    /**
     * Creates an export order.
     * 
     * @param taFile
     *            file to create an export order for.
     * @param exportDirection
     *            target export direction.
     * @return export configuration for a specific direction.
     */
    public static ExportConfig<?, ?> createExportOrder(TAFile taFile,
            MappingSide exportDirection) {
        switch (exportDirection) {
        case RIGHT:
            return new ExportConfig<ConnectorConfig, ConnectorConfig>(
                    taFile.getConnectorDataHolder1(),
                    taFile.getConnectorDataHolder2(), taFile.getMappings());
        case LEFT:
            return new ExportConfig<ConnectorConfig, ConnectorConfig>(
                    taFile.getConnectorDataHolder2(),
                    taFile.getConnectorDataHolder1(),
                    reverse(taFile.getMappings()));
        }
        throw new IllegalArgumentException("Unsupported export direction "
                + exportDirection);
    }

    private static NewMappings reverse(NewMappings mappings) {
        final NewMappings result = new NewMappings();
        for (FieldMapping mapping : mappings.getMappings()) {
            result.put(reverse(mapping));
        }
        return result;
    }

    private static FieldMapping reverse(FieldMapping mapping) {
        return new FieldMapping(mapping.getField(), mapping.getConnector2(),
                mapping.getConnector1(), mapping.isSelected());
    }
}
