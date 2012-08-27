package com.taskadapter.connector.jira.exceptions;

import java.net.URISyntaxException;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Bad URI jira exception (REST protocol).
 * @author zmd
 *
 */

public class BadURIException  extends ConnectorException {
    public BadURIException(URISyntaxException e) {
        super(e);
    }

    private static final long serialVersionUID = 1L;

}
