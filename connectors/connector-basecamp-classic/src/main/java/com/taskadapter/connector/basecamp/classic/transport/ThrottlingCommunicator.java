package com.taskadapter.connector.basecamp.classic.transport;

import org.apache.http.HttpRequest;

import com.taskadapter.connector.basecamp.classic.exceptions.CommunicationInterruptedException;
import com.taskadapter.connector.basecamp.classic.transport.throttling.BasicThrottler;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Throttling communicator.
 * 
 */
public class ThrottlingCommunicator implements Communicator {

    /**
     * Peer communicator.
     */
    private final Communicator peer;
    private final BasicThrottler throttler;

    public ThrottlingCommunicator(Communicator peer, BasicThrottler throttler) {
        this.peer = peer;
        this.throttler = throttler;
    }

    @Override
    public BasicHttpResponse sendRequest(HttpRequest request)
            throws ConnectorException {
        try {
            throttler.start();
        } catch (InterruptedException e) {
            throw new CommunicationInterruptedException(
                    "Communication interrupted while waiting for a new step");
        }
        try {
            return peer.sendRequest(request);
        } finally {
            throttler.end();
        }
    }

}
