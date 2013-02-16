package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.exceptions.BadFieldException;
import com.taskadapter.connector.basecamp.exceptions.FieldNotSetException;
import com.taskadapter.connector.basecamp.exceptions.ObjectNotFoundException;
import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.data.Messages;

class BasecampErrorFormatter implements ExceptionFormatter {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.basecamp.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof BadFieldException) {
            final String field = ((BadFieldException) e).getFieldName();
            if ("project-key".equals(field)) {
                return MESSAGES.format("error.projectKey");
            } else if ("todo-key".equals(field)) {
                return MESSAGES.format("error.todoKey");
            } else if ("account-id".equals(field)) {
                return MESSAGES.format("error.accountId");
            }
        }
        if (e instanceof FieldNotSetException) {
            final String field = ((FieldNotSetException) e).getFieldId();
            if ("project-key".equals(field)) {
                return MESSAGES.format("error.projectKey");
            } else if ("todo-key".equals(field)) {
                return MESSAGES.format("error.todoKey");
            } else if ("account-id".equals(field)) {
                return MESSAGES.format("error.accountId");
            }
        }
        if (e instanceof NotAuthorizedException) {
            return MESSAGES.format("error.notAuthorized");
        }
        if (e instanceof ObjectNotFoundException) {
            return MESSAGES.format("error.notFound");
        }

        return e.toString();
    }
}
