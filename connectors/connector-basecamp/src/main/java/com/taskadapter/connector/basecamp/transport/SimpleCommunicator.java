package com.taskadapter.connector.basecamp.transport;

import org.apache.http.HttpRequest;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Simple communicator interface.
 * 
 */
public interface SimpleCommunicator<T> {
    /**
     * Performs a request.
     * 
     * @return the response body.
     */
    T sendRequest(HttpRequest request)
            throws ConnectorException;

}
