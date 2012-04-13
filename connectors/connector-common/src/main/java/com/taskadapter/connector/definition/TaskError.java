package com.taskadapter.connector.definition;

import java.util.List;

import com.taskadapter.model.GTask;

public class TaskError {
    private GTask task;
    private List<String> errors;

    public TaskError(GTask task, List<String> errors) {
        super();
        this.task = task;
        this.errors = errors;
    }

    public GTask getTask() {
        return task;
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "TaskError [task=" + task + ", errors=" + errors + "]";
    }

}
