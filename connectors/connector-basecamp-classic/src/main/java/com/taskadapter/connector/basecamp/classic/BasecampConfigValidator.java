package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.basecamp.FieldNotSetException;
import com.taskadapter.connector.definition.WebConnectorSetup;

public class BasecampConfigValidator {
    public static void validateServerAuth(WebConnectorSetup setup) throws FieldNotSetException {
        final String apiKey = setup.apiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new FieldNotSetException("auth");
        }
        final String apiUrl = setup.host();
        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new FieldNotSetException("api-url");
        }

    }

    public static void validateProjectKey(BasecampClassicConfig config)
            throws FieldNotSetException {
        final String pKey = config.getProjectKey();
        if (pKey == null || pKey.isEmpty()) {
            throw new FieldNotSetException("project-key");
        }
    }

    public static void validateTodoList(BasecampClassicConfig config)
            throws FieldNotSetException {
        final String pKey = config.getTodoKey();
        if (pKey == null || pKey.isEmpty()) {
            throw new FieldNotSetException("todo-key");
        }
    }

}
