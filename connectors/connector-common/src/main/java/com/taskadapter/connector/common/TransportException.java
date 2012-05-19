package com.taskadapter.connector.common;

public class TransportException extends RuntimeException {
    public TransportException(String message, Exception cause) {
        super(message, cause);
    }
}
