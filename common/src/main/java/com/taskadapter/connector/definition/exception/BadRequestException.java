package com.taskadapter.connector.definition.exception;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Generic error indicating that a server rejected the request as invalid (http 400 - client error)
 */
public class BadRequestException extends ConnectorException {
    public BadRequestException(String message) {
        super(message);
    }
}
