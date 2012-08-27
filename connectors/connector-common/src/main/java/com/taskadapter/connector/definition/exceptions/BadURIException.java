package com.taskadapter.connector.definition.exceptions;

import java.net.URISyntaxException;

/**
 * Bad URI jira exception (REST protocol).
 * @author zmd
 *
 */

public class BadURIException  extends BadConfigException {
    public BadURIException(URISyntaxException e) {
        super(e);
    }

    private static final long serialVersionUID = 1L;

}
