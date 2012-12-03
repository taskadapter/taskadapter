package com.taskadapter.connector.basecamp.exceptions;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Internal failure on a remote host.
 */
public class ExternalInterlalFailure extends ConnectorException {

    public ExternalInterlalFailure() {
    }

    public ExternalInterlalFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalInterlalFailure(String message) {
        super(message);
    }

    public ExternalInterlalFailure(Throwable cause) {
        super(cause);
    }

}
