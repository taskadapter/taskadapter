package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

import java.util.ArrayList;
import java.util.Collection;

public class NewMappings {
    private Collection<FieldMapping> mappings = new ArrayList<FieldMapping>();

    public NewMappings() {
    }

    // TODO Add tests
    /**
     * Deep clone constructor.
     * @param mappings
     */
    public NewMappings(Collection<FieldMapping> mappings) {
        for (FieldMapping m : mappings) {
            this.mappings.add(new FieldMapping(m.getField(), m.getConnector1(), m.getConnector2(), m.isSelected()));
        }
    }

    /**
     * Finds and returns a mapping specification for a GTask field. If no
     * mapping is found, returns <code>null</code>.
     * @param field field to find.
     * @return field mapping or <code>null</code> if no mapping was defined for
     * a <code>field</code>.
     * @deprecated this method should not be used at all. It can't reliable find
     * fields for remote-ids.
     */
    @Deprecated
    public FieldMapping getMapping(GTaskDescriptor.FIELD field) {
        if (field == null) {
            return null;
        }
        // TODO !! very inefficient, but ok for the prototype. fix before merging into master.
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
