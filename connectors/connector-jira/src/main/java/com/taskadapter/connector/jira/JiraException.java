package com.taskadapter.connector.jira;

import org.apache.axis.AxisFault;

import java.rmi.RemoteException;

/**
 * Filters the bad string prefix Jira API puts into the error message
 *
 * @author Alexey Skorokhodov
 */
@SuppressWarnings("serial")
@Deprecated
public class JiraException extends RuntimeException {
    private final static String[] PREFIXES_TO_DELETE = new String[]{
            "com.atlassian.jira.rpc.exception.RemoteAuthenticationException: ",
            "com.atlassian.jira.rpc.exception.RemoteValidationException: "};
    private final static String SUFFIX_EMPTY_BRACES = " : []";

    private String errorMessage;

    public JiraException(RemoteException e) {
        errorMessage = ((AxisFault) e).getFaultReason();
        for (String s : PREFIXES_TO_DELETE) {
            if (errorMessage.startsWith(s)) {
                errorMessage = errorMessage.substring(s.length());
            }
        }
        if (errorMessage.endsWith(SUFFIX_EMPTY_BRACES)) {
            errorMessage = errorMessage.substring(0, errorMessage.length()
                    - SUFFIX_EMPTY_BRACES.length());
        }
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
