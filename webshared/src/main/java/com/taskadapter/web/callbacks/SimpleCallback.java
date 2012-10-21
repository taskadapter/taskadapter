package com.taskadapter.web.callbacks;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Simple data callback.
 */
public interface SimpleCallback {
	/**
	 * Passes a query.
	 */
	public void callBack() throws ConnectorException;

}
