package com.taskadapter.connector.mantis;

/**
 * AuthenticationException is thrown when
 * required element or property is not set
 */
public class RequiredItemException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequiredItemException(String msg) {
        super(msg);
    }
}