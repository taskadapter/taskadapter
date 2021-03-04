package com.taskadapter.config;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.exception.FieldNotMappedException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.webui.config.EditableFieldMapping;

public class MappingsValidator {

    public static void validate(Iterable<EditableFieldMapping> mappings) throws BadConfigException {
        validateAllSelectedFieldsMappedToSomething(mappings);
    }

    private static void validateAllSelectedFieldsMappedToSomething(Iterable<EditableFieldMapping> mappings)
            throws FieldNotMappedException {
        for (EditableFieldMapping row : mappings) {
            var valid = present(row.getFieldInConnector1()) && present(row.getFieldInConnector2())
                    ||
                    (present(row.getFieldInConnector1()) && empty(row.getFieldInConnector2()) && present(row.getDefaultValue())
                            ||
                            (present(row.getFieldInConnector2()) && empty(row.getFieldInConnector1()) && present(row.getDefaultValue())));

            if (row.getSelected() && !valid) {
                var string = Strings.nullToEmpty(row.getFieldInConnector1());
                if (present(string) && present(row.getFieldInConnector2())) {
                    string = " ";
                }

                string += Strings.nullToEmpty(row.getFieldInConnector2());
                throw new FieldNotMappedException(string);
            }
        }
    }

    private static boolean empty(String string) {
        return Strings.isNullOrEmpty(string);
    }

    private static boolean present(String string) {
        return !empty(string);
    }
}
