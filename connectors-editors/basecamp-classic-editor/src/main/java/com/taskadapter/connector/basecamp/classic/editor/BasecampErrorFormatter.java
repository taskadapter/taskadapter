package com.taskadapter.connector.basecamp.classic.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.basecamp.FieldNotSetException;
import com.taskadapter.connector.basecamp.classic.exceptions.ObjectNotFoundException;
import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.data.Messages;

class BasecampErrorFormatter implements ExceptionFormatter {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.basecamp.classic.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof ProjectNotSetException) {
            return MESSAGES.get("basecampclassic.error.projectKeyNotSet");
        }

        if (e instanceof FieldNotSetException) {
            String field = ((FieldNotSetException) e).getField();
            String message = MESSAGES.format("error." + field);
            if (!Strings.isNullOrEmpty(message)) {
                return message;
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
