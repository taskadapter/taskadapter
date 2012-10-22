package com.taskadapter.web;

/**
 * Interface for error conversion and formatting.
 * 
 */
public interface ExceptionFormatter {

    /**
     * Requests to format anh error. If error is not supported (not a custom
     * error), this method may safelly return <code>null</code>.
     * 
     * @param e
     *            error to format.
     * @return formatted error.
     */
    String formatError(Throwable e);

}