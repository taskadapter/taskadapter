package com.taskadapter.connector.definition;

import java.util.List;

/**
 * Task errors collection.
 * 
 * @author maxkar
 * 
 * @param <T>
 *            error type.
 */
public final class TaskErrors<T> {
    private final List<TaskError<T>> errors;
    private final List<T> generalErrors;

    public TaskErrors(List<TaskError<T>> errors, List<T> generalErrors) {
        super();
        this.errors = errors;
        this.generalErrors = generalErrors;
    }

    public List<TaskError<T>> getErrors() {
        return errors;
    }

    public List<T> getGeneralErrors() {
        return generalErrors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty() || !generalErrors.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result
                + ((generalErrors == null) ? 0 : generalErrors.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskErrors<?> other = (TaskErrors<?>) obj;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (generalErrors == null) {
            if (other.generalErrors != null)
                return false;
        } else if (!generalErrors.equals(other.generalErrors))
            return false;
        return true;
    }

}
