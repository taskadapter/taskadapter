package com.taskadapter.connector.definition.exceptions;

/**
 * "Bad configuration" exception. This exception is thrown whenever
 * configuration is inappropriate for a given operation.
 * 
 * @author maxkar
 * 
 */
public class BadConfigException extends ConnectorException {
    private static final long serialVersionUID = 1L;

    public BadConfigException() {
        super();
    }

    public BadConfigException(String message) {
        super(message);
    }

    public BadConfigException(Throwable cause) {
        super(cause);
    }

    public BadConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
