package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.EntityProcessingException;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineProcessingException;

/**
 * Redmine exceptions.
 * 
 * @author maxkar
 * 
 */
public final class RedmineExceptions {
    public static ConnectorException convertException(RedmineException e) {
        if (e instanceof RedmineProcessingException) {
            final RedmineProcessingException rpe = (RedmineProcessingException) e;
            return new EntityProcessingException(rpe.getMessage());
        }
        return new CommunicationException(e);
    }
}
