package com.taskadapter.core;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Notifies failure to update "remote ID's". 
 * @author maxkar
 *
 */
public class RemoteIdUpdateFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public RemoteIdUpdateFailedException(ConnectorException cause) {
        super(cause);
    }

}
