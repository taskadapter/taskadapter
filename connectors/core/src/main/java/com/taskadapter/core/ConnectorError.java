package com.taskadapter.core;

/**
 * @param <T> error type.
 */
public final class ConnectorError<T> {
    /**
     * Error description.
     */
    private final T error;

    private final String connectorId;

    public ConnectorError(T error, String connectorId) {
        this.error = error;
        this.connectorId = connectorId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + ((connectorId == null) ? 0 : connectorId.hashCode());
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
        ConnectorError<?> other = (ConnectorError<?>) obj;
        if (error == null) {
            if (other.error != null)
                return false;
        } else if (!error.equals(other.error))
            return false;
        if (connectorId == null) {
            if (other.connectorId != null)
                return false;
        } else if (!connectorId.equals(other.connectorId))
            return false;
        return true;
    }

    public T getError() {
        return error;
    }

    public String getConnectorId() {
        return connectorId;
    }

}
