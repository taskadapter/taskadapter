package com.taskadapter.connector.basecamp.classic.transport;

import org.apache.http.HttpRequest;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

public interface Communicator {

    /**
     * Performs a request.
     * 
     * @return the response body.
     */
    public abstract BasicHttpResponse sendRequest(HttpRequest request)
            throws ConnectorException;

}