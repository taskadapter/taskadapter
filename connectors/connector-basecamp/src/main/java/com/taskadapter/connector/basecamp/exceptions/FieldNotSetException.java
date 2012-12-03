package com.taskadapter.connector.basecamp.exceptions;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldNotSetException extends BadConfigException {
    private final String fieldId;

    public FieldNotSetException(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldId() {
        return fieldId;
    }

}
