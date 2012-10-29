package com.taskadapter.web.uiapi;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTaskDescriptor;

import java.util.Collection;

public class MappingFixer {

    // TODO why does this method do two things instead of 1?
    /**
     * Fixes mappings.
     * Remove "unsupported" mappings.
     * Add new mappings (in <code>newMappingsEnabled</code> state).
     *
     * @param mappings           mappings to fix.
     * @param newMappingsEnabled state for the new (added) mappings.
     */
    static NewMappings fixMappings(NewMappings mappings, AvailableFields fields1,
                                   AvailableFields fields2, boolean newMappingsEnabled) {
        final Collection<GTaskDescriptor.FIELD> firstFields = fields1.getSupportedFields();
        final Collection<GTaskDescriptor.FIELD> secondFields = fields2.getSupportedFields();

        final NewMappings result = new NewMappings();

        if (secondFields.contains(GTaskDescriptor.FIELD.REMOTE_ID)) {
            final FieldMapping saved = findRemote(mappings, MappingSide.RIGHT);
            if (saved != null) {
                result.put(saved);
            } else {
                result.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, null,
                        getDefaultFieldValue(GTaskDescriptor.FIELD.REMOTE_ID, fields2),
                        newMappingsEnabled));
            }
        }

        if (firstFields.contains(GTaskDescriptor.FIELD.REMOTE_ID)) {
            final FieldMapping saved = findRemote(mappings, MappingSide.LEFT);
            if (saved != null) {
                result.put(saved);
            } else {
                result.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID,
                        getDefaultFieldValue(GTaskDescriptor.FIELD.REMOTE_ID, fields1), null,
                        newMappingsEnabled));
            }
        }

        for (GTaskDescriptor.FIELD field : GTaskDescriptor.FIELD.values()) {
            if (field == GTaskDescriptor.FIELD.ID || field == GTaskDescriptor.FIELD.REMOTE_ID) {
                continue;
            }

            if (!firstFields.contains(field) || !secondFields.contains(field)) {
                continue;
            }

            final FieldMapping oldMapping = mappings.getMapping(field);
            if (oldMapping != null) {
                result.put(oldMapping);
                continue;
            }

            final FieldMapping newMapping = new FieldMapping(field,
                    getDefaultFieldValue(field, fields1), getDefaultFieldValue(
                    field, fields2), newMappingsEnabled);
            result.put(newMapping);
        }

        return result;
    }

    private static FieldMapping findRemote(NewMappings mappings,
                                           MappingSide sideWithRemoteId) {
        for (FieldMapping mapping : mappings.getMappings()) {
            if (mapping.getField() != GTaskDescriptor.FIELD.REMOTE_ID) {
                continue;
            }
            String targetMapping;
            switch (sideWithRemoteId) {
            case LEFT:
                targetMapping = mapping.getConnector1();
                break;
            case RIGHT:
                targetMapping = mapping.getConnector2();
                break;
            default:
                throw new IllegalArgumentException("Unsupported mapping side "
                        + sideWithRemoteId);
            }
            if (targetMapping != null) {
                return mapping;
            }
        }
        return null;
    }

    private static String getDefaultFieldValue(GTaskDescriptor.FIELD field, AvailableFields fields1) {
        return fields1.getDefaultValue(field);
    }

}
