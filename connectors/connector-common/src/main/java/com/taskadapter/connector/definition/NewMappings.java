package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

import java.util.ArrayList;
import java.util.Collection;

public class NewMappings {
    private Collection<FieldMapping> mappings = new ArrayList<FieldMapping>();

    public FieldMapping getMapping(GTaskDescriptor.FIELD field) {
        // TODO !!! very inefficient, but ok for the prototype. fix before merging into master.
        for (FieldMapping mapping : mappings) {
            if (mapping.getField().equals(field)) {
                return mapping;
            }
        }
        // TODO !!! return some "NO_MAPPING" instead of null.
        // maybe http://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained#Optional
        return null;
    }

    public void put(FieldMapping fieldMapping) {
        mappings.add(fieldMapping);
    }

    public Collection<FieldMapping> getMappings() {
        return mappings;
    }
}
