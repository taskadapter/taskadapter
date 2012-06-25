package com.taskadapter.connector.common;

@Deprecated
public class TransportException extends RuntimeException {
    public TransportException(String message, Exception cause) {
        super(message, cause);
    }
}
