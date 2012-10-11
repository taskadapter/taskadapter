package com.taskadapter.connector;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;

public class MappingBuilder {
    public static Mappings build(NewMappings newMappings, MappingSide leftRight) {
        Mappings mappings = new Mappings();
        for (FieldMapping fieldMapping : newMappings.getMappings()) {
            mappings.setMapping(fieldMapping.getField(), fieldMapping.isSelected(), getSideMappedTo(fieldMapping, leftRight));
        }
        return mappings;
    }
    
    public static String getSideMappedTo(FieldMapping fieldMapping, MappingSide side) {
        switch (side) {
            case LEFT:
                return fieldMapping.getConnector1();
            case RIGHT:
                return fieldMapping.getConnector2();
        }
        throw new IllegalArgumentException("Unsupported mapping direction : " + side);
    }
}
