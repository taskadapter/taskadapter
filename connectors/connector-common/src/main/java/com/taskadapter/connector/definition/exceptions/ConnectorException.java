package com.taskadapter.connector.definition.exceptions;

/**
 * General connector exceptions. It is not recommended to instantiate this
 * exception directly. Usually methods should throw descendants of this
 * exception to allow more detailed error reporting.
 * 
 * @author maxkar
 * 
 */
public class ConnectorException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConnectorException() {
        super();
    }

    public ConnectorException(String message) {
        super(message);
    }

    public ConnectorException(Throwable cause) {
        super(cause);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

}
