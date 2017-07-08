package com.taskadapter.connector;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTaskDescriptor.FIELD;

public class MappingBuilder {
    public static Mappings build(NewMappings newMappings, MappingSide leftRight) {
        Mappings mappings = new Mappings();
        for (FieldMapping fieldMapping : newMappings.getMappings()) {
            final String mappingTarget = getSideMappedTo(fieldMapping, leftRight);
            if (!fieldMapping.getField().equals(FIELD.REMOTE_ID.name()) || mappingTarget != null) {
                mappings.setMapping(fieldMapping.getField(), fieldMapping.isSelected(), mappingTarget, fieldMapping.getDefaultValue());
            }
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
