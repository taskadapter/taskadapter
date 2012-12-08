package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.exceptions.BadFieldException;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.data.Messages;

class BasecampErrorFormatter implements ExceptionFormatter {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.basecamp.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public String formatError(Throwable e) {
        if (e instanceof BadFieldException && ((BadFieldException) e).getFieldName().equals("project-key")) {
            return MESSAGES.format("error.projectKey");
        }
        return e.toString();
    }
}
