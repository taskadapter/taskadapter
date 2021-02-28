package com.taskadapter.connector.definition.exception;

import com.taskadapter.connector.common.FieldPrettyNameBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.Field;
import scala.collection.Seq;

public class FieldConversionException extends ConnectorException {

    private String connectorId;
    private Field<?> field;
    private Object value;
    private String details;

    public FieldConversionException(String connectorId, Field<?> field, Object value, String details) {
        this.connectorId = connectorId;
        this.field = field;
        this.value = value;
        this.details = details;
    }

    @Override
    public String getMessage() {
        String valueString;
        if (value instanceof Seq) {
            var seq = (Seq<?>) value;
            if (seq.isEmpty()) {
                valueString = "Empty collection";
            } else {
                valueString = "Collection of (" + seq.mkString(",") + ")";
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
