package com.taskadapter.connector.basecamp.classic.transport;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

public class BasicAuthenticator implements Communicator {
    /**
     * Header value.
     */
    private String authKey;

    /**
     * Peer communicator.
     */
    private final Communicator peer;

    public BasicAuthenticator(Communicator peer, String login, String password) {
        this.peer = peer;
        try {
            authKey = "Basic: "
                    + "\""
                    + Base64.encodeBase64String(
                            (login + ':' + password).getBytes("UTF-8")).trim()
                    + "\"";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BasicHttpResponse sendRequest(HttpRequest request)
            throws ConnectorException {
        if (authKey != null)
            request.addHeader("Authorization", authKey);
        return peer.sendRequest(request);
    }

}
