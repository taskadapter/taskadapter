package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.connector.basecamp.classic.exceptions.FieldNotSetException;

public class BasecampConfigValidator {
    public static void validateServerAuth(BasecampConfig config)
            throws FieldNotSetException {
        final String apiKey = config.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new FieldNotSetException("auth");
        }
        final String apiUrl = config.getServerUrl();
        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new FieldNotSetException("api-url");
        }

    }

    public static void validateProjectKey(BasecampConfig config)
            throws FieldNotSetException {
        final String pKey = config.getProjectKey();
        if (pKey == null || pKey.isEmpty()) {
            throw new FieldNotSetException("project-key");
        }
    }

    public static void validateTodoList(BasecampConfig config)
            throws FieldNotSetException {
        final String pKey = config.getTodoKey();
        if (pKey == null || pKey.isEmpty()) {
            throw new FieldNotSetException("todo-key");
        }
    }

}
