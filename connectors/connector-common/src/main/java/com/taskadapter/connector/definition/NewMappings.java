package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

import java.util.ArrayList;
import java.util.Collection;

public class NewMappings {
    private Collection<FieldMapping> mappings = new ArrayList<FieldMapping>();

    /**
     * Finds and returns a mapping specification for a GTask field. If no
     * mapping is found, returns <code>null</code>.
     * @param field field to find.
     * @return field mapping or <code>null</code> if no mapping was defined for
     * a <code>field</code>.
     */
    public FieldMapping getMapping(GTaskDescriptor.FIELD field) {
        if (field == null) {
            return null;
        }
        // TODO !!! very inefficient, but ok for the prototype. fix before merging into master.
        for (FieldMapping mapping : mappings) {
            if (mapping.getField().equals(field)) {
                return mapping;
            }
        }
        return null;
    }

    public void put(FieldMapping fieldMapping) {
        mappings.add(fieldMapping);
    }

    public Collection<FieldMapping> getMappings() {
        return mappings;
    }
}
