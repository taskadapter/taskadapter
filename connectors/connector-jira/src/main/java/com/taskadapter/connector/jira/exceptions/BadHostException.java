package com.taskadapter.connector.jira.exceptions;

import java.net.MalformedURLException;

import com.taskadapter.connector.definition.exceptions.BadConfigException;

/**
 * "Bad host" jira exception. 
 * @author maxkar
 *
 */
public class BadHostException extends BadConfigException {
    public BadHostException(MalformedURLException e) {
        super(e);
    }

    private static final long serialVersionUID = 1L;

}
