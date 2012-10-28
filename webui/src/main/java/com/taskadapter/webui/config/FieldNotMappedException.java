package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTaskDescriptor;

public class FieldNotMappedException extends BadConfigException {
    private GTaskDescriptor.FIELD field;

    public FieldNotMappedException(GTaskDescriptor.FIELD field) {
        this.field = field;
    }

    public GTaskDescriptor.FIELD getField() {
        return field;
    }
}
