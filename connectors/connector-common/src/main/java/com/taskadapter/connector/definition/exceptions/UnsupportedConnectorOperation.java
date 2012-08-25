package com.taskadapter.connector.definition.exceptions;

/**
 * Notifies an unsupported connector operation.
 * 
 * @author maxkar
 * 
 */
public class UnsupportedConnectorOperation extends ConnectorException {

    private static final long serialVersionUID = 1L;

    public UnsupportedConnectorOperation() {
        super();
    }

    public UnsupportedConnectorOperation(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedConnectorOperation(String message) {
        super(message);
    }

    public UnsupportedConnectorOperation(Throwable cause) {
        super(cause);
    }
}
