package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

/**
 * Default task type is not set. It is required when user does not select a
 * "task field type" in mapping. And event if that field is mapped, it is
 * required because we cannot ensure that all possible task types are
 * covered in jira.
 */
public class DefaultTaskTypeNotSetException extends BadConfigException {
}
