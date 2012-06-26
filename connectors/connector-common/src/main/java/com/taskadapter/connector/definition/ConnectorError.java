package com.taskadapter.connector.definition;

/**
 * Connector error definition.
 * 
 * @author maxkar
 * 
 * @param <T>
 *            error type.
 */
public final class ConnectorError<T> {
    /**
     * Error description.
     */
    private final T error;

    /**
     * Connector type id.
     */
    private final String typeId;

    public ConnectorError(T error, String typeId) {
        this.error = error;
        this.typeId = typeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
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
        if (typeId == null) {
            if (other.typeId != null)
                return false;
        } else if (!typeId.equals(other.typeId))
            return false;
        return true;
    }

    public T getError() {
        return error;
    }

    public String getTypeId() {
        return typeId;
    }

}
