package com.taskadapter.connector;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;

public class MappingBuilder {
    public static Mappings build(NewMappings newMappings, MappingSide leftRight) {
        Mappings mappings = new Mappings();
        for (FieldMapping fieldMapping : newMappings.getMappings()) {
            mappings.addField(fieldMapping.getField(), getSideMappedTo(fieldMapping, leftRight),fieldMapping.isSelected());
        }
        return mappings;
    }
    
    public static String getSideMappedTo(FieldMapping fieldMapping, MappingSide side) {
        switch (side) {
            case LEFT:
                return fieldMapping.getLeft();
            case RIGHT:
                return fieldMapping.getRight();
        }
        throw new IllegalArgumentException("Unsupported mapping direction : " + side);
    }
}
