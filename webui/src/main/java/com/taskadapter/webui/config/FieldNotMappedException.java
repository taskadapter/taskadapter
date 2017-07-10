package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldNotMappedException extends BadConfigException {
    private FieldMapping field;

    public FieldNotMappedException(FieldMapping field) {
        this.field = field;
    }

    public FieldMapping getField() {
        return field;
    }
}
