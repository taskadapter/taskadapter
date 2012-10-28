package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldAlreadyMappedException extends BadConfigException {
    private String value;

    public FieldAlreadyMappedException(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
