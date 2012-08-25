package com.taskadapter.connector.definition;

/**
 * Synchronous operation result. Contains both possible result and operation
 * warnings/errors.
 * 
 * @param <R>
 *            result type.
 * @param <E>
 *            error result type.
 */
public class SyncResult<R, E> {
    private final R result;
    private final E errors;

    public SyncResult(R result, E errors) {
        super();
        this.result = result;
        this.errors = errors;
    }

    public R getResult() {
        return result;
    }

    public E getErrors() {
        return errors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result
                + ((this.result == null) ? 0 : this.result.hashCode());
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
        SyncResult<?, ?> other = (SyncResult<?, ?>) obj;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        return true;
    }

}
