package com.taskadapter.connector.definition.exceptions;

/**
 * Connector communication exception. Notifies any communication/data transfer
 * problem between connector and connector target.
 * 
 * @author maxkar
 * 
 */
public class CommunicationException extends ConnectorException {

    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    public CommunicationException() {
        super();
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(Throwable cause) {
        super(cause);
    }

}
