package com.taskadapter.connector.definition;

import com.taskadapter.model.GTask;

public class TaskError {
    private GTask task;
    private Throwable error;

    public TaskError(GTask task, Throwable error) {
        super();
        this.task = task;
        this.error = error;
    }

    public GTask getTask() {
        return task;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "TaskError [task=" + task + ", error=" + error + "]";
    }

}
