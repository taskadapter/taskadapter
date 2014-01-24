package com.taskadapter.connector.jira;

import java.util.Collection;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

/**
 * Jira configuration exception. Contains validation message tags.
 */
public final class JiraConfigException extends BadConfigException {
    private final Collection<JiraValidationErrorKind> errors;

    public JiraConfigException(Collection<JiraValidationErrorKind> errors) {
        this.errors = errors;
    }

    public Collection<JiraValidationErrorKind> getErrors() {
        return errors;
    }
}
