package com.taskadapter.connector.definition.exceptions;

public class NotAuthorizedException extends BadConfigException {
    private static final long serialVersionUID = 1L;

    public NotAuthorizedException(String faultString) {
        super(faultString);
    }

    public NotAuthorizedException() {
        super("not authorized");
    }
}
