package com.taskadapter.connector.definition.exceptions;

public class NotAuthorizedException extends ConnectorException {
    public NotAuthorizedException(String faultString) {
        super(faultString);
    }

    public NotAuthorizedException() {
        super("");
    }
}
