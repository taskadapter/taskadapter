package com.taskadapter.connector.basecamp.transport.throttling;

import com.taskadapter.connector.definition.exceptions.CommunicationException;

public class ThrottlingException extends CommunicationException {

    /**
     * Timeout to wait. If negative, then no timeout was specified by a peer.
     */
    public final int timeoutSec;

    public ThrottlingException(int timeoutSec) {
        this.timeoutSec = timeoutSec;
    }
}
