package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTaskDescriptor;

import java.util.HashSet;
import java.util.Set;

public class MappingsValidator {
    static void validate(NewMappings mappings) throws BadConfigException {
        validateAllSelectedFieldsMappedToSomething(mappings);
        validateFieldsAreOnlyUsedOnce(mappings);
    }

    private static void validateAllSelectedFieldsMappedToSomething(NewMappings mappings) throws FieldNotMappedException {
        for (FieldMapping mapping : mappings.getMappings()) {
            boolean notMapped;
            if (mapping.getField() == GTaskDescriptor.FIELD.REMOTE_ID) {
                // TODO !!! this is a hack.  fix Remote ID mapping.
                notMapped = mapping.getConnector1() == null && mapping.getConnector2() == null;
            } else {
                notMapped = mapping.getConnector1() == null || mapping.getConnector2() == null;
            }
            if (mapping.isSelected() && notMapped) {
                throw new FieldNotMappedException(mapping.getField());
            }
        }
    }

    private static void validateFieldsAreOnlyUsedOnce(NewMappings mappings) throws FieldAlreadyMappedException {
        // TODO !!! also need to check for the "internal" fields used by our MSP connectors.
        // see FIELD_WORK_UNDEFINED and FIELD_DURATION_UNDEFINED in MSXMLFileWriter
        Set<String> connector1Values = new HashSet<String>();
        Set<String> connector2Values = new HashSet<String>();
        for (FieldMapping mapping : mappings.getMappings()) {
            if (connector1Values.contains(mapping.getConnector1())) {
                throw new FieldAlreadyMappedException(mapping.getConnector1());
            }
            if (connector2Values.contains(mapping.getConnector2())) {
                throw new FieldAlreadyMappedException(mapping.getConnector2());
            }
            connector1Values.add(mapping.getConnector1());
            connector2Values.add(mapping.getConnector2());
        }
    }

}
