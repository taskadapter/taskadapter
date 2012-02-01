package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;

/**
 * @author Alexey Skorokhodov
 */
public interface Validatable {
    void validate() throws ValidationException;
}

