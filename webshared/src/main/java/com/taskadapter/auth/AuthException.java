package com.taskadapter.auth;

/**
 * Authentication exception. Any exception, including corrupted data, etc...
 */
public final class AuthException extends Exception {

    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
