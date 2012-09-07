package com.taskadapter.connector.github;

import java.io.IOException;
import java.net.HttpRetryException;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.NotAuthorizedException;

public final class GithubUtils {
    public static ConnectorException convertException(IOException e) {
        if (e instanceof HttpRetryException) {
            int errorCode = ((HttpRetryException) e).responseCode();
            if (errorCode == 401) {
                return new NotAuthorizedException();
            }
        }
        return new CommunicationException(e);
    }
}
