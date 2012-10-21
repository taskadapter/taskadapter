package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

public interface Validatable {
    void validate() throws BadConfigException;
}

