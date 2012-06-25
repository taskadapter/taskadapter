package com.taskadapter.connector.github;

import java.io.IOException;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Github utilities.
 * 
 * @author maxkar
 * 
 */
public final class GithubUtils {
    public static ConnectorException convertException(IOException e) {
        return new CommunicationException(e);
    }
}
