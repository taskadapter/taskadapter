package com.taskadapter.connector.definition;

import com.taskadapter.model.GTask;

/**
 * Defines a task error. 
 *
 * @param <T> error definition type.
 */
// TODO Alexey: I see we have only one usage of this class with Throwable type. This "<T>" generic can be deleted.
public class TaskError<T> {
    private GTask task;
    private T errors;

    public TaskError(GTask task, T errors) {
        super();
        this.task = task;
        this.errors = errors;
    }

    public GTask getTask() {
        return task;
    }

    public T getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "TaskError [task=" + task + ", errors=" + errors + "]";
    }

}
