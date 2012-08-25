package com.taskadapter.connector.jira;

import java.net.MalformedURLException;

import java.net.URISyntaxException;
import java.rmi.RemoteException;

import com.taskadapter.connector.definition.exceptions.BadURIException;
import org.apache.axis.AxisFault;

import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.jira.exceptions.BadHostException;

public final class JiraUtils {
    private final static String[] PREFIXES_TO_DELETE = new String[] {
            "com.atlassian.jira.rpc.exception.RemoteAuthenticationException: ",
            "com.atlassian.jira.rpc.exception.RemoteValidationException: " };
    private final static String SUFFIX_EMPTY_BRACES = " : []";

    public static ConnectorException convertException(RemoteException e) {
        if (e instanceof RemoteAuthenticationException) {
            return new ConnectorException(((RemoteAuthenticationException) e).getFaultString());
        }
        
        if (!(e instanceof AxisFault))
            return new CommunicationException(e);
        
        String errorMessage = ((AxisFault) e).getFaultReason();
        for (String s : PREFIXES_TO_DELETE) {
            if (errorMessage.startsWith(s)) {
                errorMessage = errorMessage.substring(s.length());
            }
        }
        if (errorMessage.endsWith(SUFFIX_EMPTY_BRACES)) {
            errorMessage = errorMessage.substring(0, errorMessage.length()
                    - SUFFIX_EMPTY_BRACES.length());
        }
        return new CommunicationException(errorMessage, e);
    }

    public static ConnectorException convertException(MalformedURLException e) {
        return new BadHostException(e);
    }

    public static ConnectorException convertException(URISyntaxException e) {
        return new BadURIException(e);
    }


}
