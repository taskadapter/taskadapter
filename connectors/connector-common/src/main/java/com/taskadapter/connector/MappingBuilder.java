package com.taskadapter.connector;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.NewMappings;

public class MappingBuilder {
    public static Mappings build(NewMappings newMappings, MappingSide leftRight) {
        Mappings mappings = new Mappings();
        for (FieldMapping fieldMapping : newMappings.getMappings()) {
            switch (leftRight) {
                case LEFT:
                    mappings.addField(fieldMapping.getField(), fieldMapping.getLeft());
                    break;
                case RIGHT:
                    mappings.addField(fieldMapping.getField(), fieldMapping.getRight());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return mappings;
    }
}
