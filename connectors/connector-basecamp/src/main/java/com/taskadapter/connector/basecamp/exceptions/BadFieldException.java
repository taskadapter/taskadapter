package com.taskadapter.connector.basecamp.exceptions;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class BadFieldException extends BadConfigException {
    private final String fieldName;

    public BadFieldException(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
