package com.taskadapter.connector.jira;

/**
 * Codes for jira validation errors (because config may be invalid for several
 * reasons at once).
 */
public enum JiraValidationErrorKind {
    /** Jira target host is not set, cannot save into the void or load from it. */
    HOST_NOT_SET,
    /** Jira project is not set. Tasks require a project to exist. */
    PROJECT_NOT_SET,
    /**
     * Default task type is not set. It is required when user does not select a
     * "task field type" in mapping. And event if that field is mapped, it is
     * required because we cannot ensure that all possible task types are
     * covered in jira.
     */
    DEFAULT_TASK_TYPE_NOT_SET,
    /** Similar to {@link #DEFAULT_TASK_TYPE_NOT_SET}, but for sutbasks. */
    DEFAULT_SUBTASK_TYPE_NOT_SET,
    /** Data query id is not set, so data can't be loaded. */
    QUERY_ID_NOT_SET,
}
