package com.taskadapter.webui.export;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.MappingBuilder;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;

public class DirectionResolver {

    private final TAFile file;
    private final MappingSide exportDirection;

    private Mappings sourceMappings;
    private Mappings destinationMappings;
    private ConnectorDataHolder sourceDataHolder;
    private ConnectorDataHolder destinationDataHolder;

    DirectionResolver(TAFile file, MappingSide exportDirection) {
        this.file = file;
        this.exportDirection = exportDirection;
        resolve();
    }

    private void resolve() {
        switch (exportDirection) {
            case RIGHT:
                sourceDataHolder = file.getConnectorDataHolder1();
                destinationDataHolder = file.getConnectorDataHolder2();
                break;
            case LEFT:
                sourceDataHolder = file.getConnectorDataHolder2();
                destinationDataHolder = file.getConnectorDataHolder1();
                break;
            default:
                throw new IllegalArgumentException();
        }
        sourceMappings = MappingBuilder.build(file.getMappings(), getOppositeSide(exportDirection));
        destinationMappings = MappingBuilder.build(file.getMappings(), exportDirection);
    }

    private MappingSide getOppositeSide(MappingSide side) {
        switch (side) {
            case LEFT:
                return MappingSide.RIGHT;
            case RIGHT:
                return MappingSide.LEFT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getDestinationConnectorId() {
        return destinationDataHolder.getType();
    }

    public String getSourceConnectorId() {
        return sourceDataHolder.getType();
    }

    public Mappings getSourceMappings() {
        return sourceMappings;
    }

    public Mappings getDestinationMappings() {
        return destinationMappings;
    }

    public ConnectorDataHolder getSourceDataHolder() {
        return sourceDataHolder;
    }

    public ConnectorDataHolder getDestinationDataHolder() {
        return destinationDataHolder;
    }

    public ConnectorConfig getSourceConfig() {
        return sourceDataHolder.getData();
    }

    public ConnectorConfig getDestinationConfig() {
        return destinationDataHolder.getData();
    }

    public NewMappings getMappings() {
        return file.getMappings();
    }
}
