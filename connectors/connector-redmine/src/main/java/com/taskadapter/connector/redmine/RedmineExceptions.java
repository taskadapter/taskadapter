package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.redmineapi.RedmineException;

/**
 * Redmine exceptions.
 * 
 * @author maxkar
 * 
 */
public final class RedmineExceptions {
    public static ConnectorException convertException(RedmineException e) {
        return new CommunicationException(e);
    }
}
