package com.taskadapter.web.uiapi;

import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.FieldMapping;
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
     * @param config1            first config.
     * @param config2            second config.
     * @param newMappingsEnabled state for the new (added) mappings.
     */
    static NewMappings fixMappings(NewMappings mappings, UIConnectorConfig config1,
                                    UIConnectorConfig config2, boolean newMappingsEnabled) {
        final AvailableFields fields1 = config1.getAvailableFields();
        final AvailableFields fields2 = config2.getAvailableFields();
        final Collection<GTaskDescriptor.FIELD> firstFields = fields1.getSupportedFields();
        final Collection<GTaskDescriptor.FIELD> secondFields = fields2.getSupportedFields();

        final NewMappings result = new NewMappings();

        if (secondFields.contains(GTaskDescriptor.FIELD.REMOTE_ID)) {
            final FieldMapping saved = findRemote(mappings, false, true);
            if (saved != null) {
                result.put(saved);
            } else {
                result.put(new FieldMapping(GTaskDescriptor.FIELD.REMOTE_ID, null,
                        getDefaultFieldValue(GTaskDescriptor.FIELD.REMOTE_ID, fields2),
                        newMappingsEnabled));
            }
        }

        if (firstFields.contains(GTaskDescriptor.FIELD.REMOTE_ID)) {
            final FieldMapping saved = findRemote(mappings, true, false);
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
                                           boolean remoteLeft, boolean remoteRight) {
        for (FieldMapping mapping : mappings.getMappings()) {
            if (mapping.getField() == GTaskDescriptor.FIELD.ID && (
                    ((mapping.getConnector1() == null) != remoteLeft) ||
                            ((mapping.getConnector2() == null) != remoteRight))) {
                return mapping;
            }
        }
        return null;
    }

    // TODO !!! this is completely wrong. use real default values, not the 1st field.
    private static String getDefaultFieldValue(GTaskDescriptor.FIELD field, AvailableFields fields1) {
        final String[] values = fields1.getAllowedValues(field);
        if (values == null || values.length < 1) {
            return null;
        }
        return values[0];
    }

}
