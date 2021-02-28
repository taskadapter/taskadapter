package com.taskadapter.connector.definition.exception;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldNotMappedException extends BadConfigException {
    private final String fieldName;

    public FieldNotMappedException(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
