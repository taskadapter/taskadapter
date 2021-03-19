package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldNotSetException extends BadConfigException {
    private final String field;

    public FieldNotSetException(String field) {
        super("Field not set: " + field);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
