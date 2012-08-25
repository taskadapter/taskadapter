package com.taskadapter.connector.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TaskErrorsBuilder<E> {
    private List<TaskError<E>> errors = new ArrayList<TaskError<E>>();
    private List<E> generalErrors = new ArrayList<E>();

    public void addError(TaskError<E> e) {
        errors.add(e);
    }
    
    public void addErrors(Collection<TaskError<E>> e) {
        errors.addAll(e);
    }

    public void addGeneralError(E e) {
        generalErrors.add(e);
    }

    public void addGeneralErrors(Collection<E> e) {
        generalErrors.addAll(e);
    }

    public TaskErrors<E> getResult() {
        return new TaskErrors<E>(
                Collections
                        .unmodifiableList(new ArrayList<TaskError<E>>(errors)),
                new ArrayList<E>(generalErrors));
    }
}
