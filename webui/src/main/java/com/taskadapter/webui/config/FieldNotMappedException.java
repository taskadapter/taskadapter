package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldNotMappedException extends BadConfigException {
    private String field;

    public FieldNotMappedException(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
