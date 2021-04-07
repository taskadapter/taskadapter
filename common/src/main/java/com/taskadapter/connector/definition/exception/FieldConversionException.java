package com.taskadapter.connector.definition.exception;

import com.taskadapter.connector.common.FieldPrettyNameBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.Field;

import java.util.List;

public class FieldConversionException extends ConnectorException {

    private final String connectorId;
    private final Field<?> field;
    private final Object value;
    private final String details;

    public FieldConversionException(String connectorId, Field<?> field, Object value, String details) {
        this.connectorId = connectorId;
        this.field = field;
        this.value = value;
        this.details = details;
    }

    @Override
    public String getMessage() {
        String valueString;
        if (value instanceof List) {
            var list = (List<?>) value;
            if (list.isEmpty()) {
                valueString = "Empty collection";
            } else {
                valueString = "Collection of (" + String.join(",", (List<String>) list) + ")";
            }
        } else {
            valueString = "Value '" + value + "'";
        }

        return valueString + " cannot be saved in field '" + FieldPrettyNameBuilder.getPrettyFieldName(field) +
                "' by " + connectorId + " connector. " +
                " Reason: " + details +
                ". Please verify that the field mapping for this field makes sense. " +
                "Type checks will be improved in future app versions";
    }
}
