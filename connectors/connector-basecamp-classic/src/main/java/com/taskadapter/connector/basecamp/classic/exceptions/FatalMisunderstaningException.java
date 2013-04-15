package com.taskadapter.connector.basecamp.classic.exceptions;

import com.taskadapter.connector.definition.exceptions.CommunicationException;

public class FatalMisunderstaningException extends CommunicationException {

    public FatalMisunderstaningException() {
        super();
    }

    public FatalMisunderstaningException(String message, Throwable cause) {
        super(message, cause);
    }

    public FatalMisunderstaningException(String message) {
        super(message);
    }

    public FatalMisunderstaningException(Throwable cause) {
        super(cause);
    }

}
