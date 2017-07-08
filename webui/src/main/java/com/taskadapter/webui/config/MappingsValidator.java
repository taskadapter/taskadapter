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
            if (mapping.getField().equals(GTaskDescriptor.FIELD.REMOTE_ID.name())) {
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
        Set<String> connector1Values = new HashSet<>();
        Set<String> connector2Values = new HashSet<>();
        for (FieldMapping mapping : mappings.getMappings()) {
            String connector1Value = mapping.getConnector1();
            // it is null if no value is selected in the dropdown with MSP Text fields.
            if (connector1Value != null && connector1Values.contains(connector1Value)) {
                throw new FieldAlreadyMappedException(connector1Value);
            }
            String connector2Value = mapping.getConnector2();
            // it is null if no value is selected in the dropdown with MSP Text fields.
            if (connector2Value != null && connector2Values.contains(connector2Value)) {
                throw new FieldAlreadyMappedException(connector2Value);
            }
            connector1Values.add(connector1Value);
            connector2Values.add(connector2Value);
        }
    }

}
