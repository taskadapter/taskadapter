package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public class FieldNotMappedException extends BadConfigException {

    public FieldNotMappedException(String s) {
        super(s);
    }
}
