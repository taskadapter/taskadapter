package com.taskadapter.connector.basecamp.exceptions;

import com.taskadapter.connector.definition.exceptions.CommunicationException;

/**
 * "Communication was interrupted" exception. Thrown when a user chooses to
 * cancel an operation.
 * 
 * 
 */
public class CommunicationInterruptedException extends CommunicationException {

    public CommunicationInterruptedException() {
        super();
    }

    public CommunicationInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommunicationInterruptedException(String message) {
        super(message);
    }

    public CommunicationInterruptedException(Throwable cause) {
        super(cause);
    }

}
