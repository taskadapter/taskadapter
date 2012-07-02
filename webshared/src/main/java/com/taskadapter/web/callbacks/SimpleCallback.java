package com.taskadapter.web.callbacks;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Simple data callback.
 * 
 * @author maxkar
 * 
 */
public interface SimpleCallback {
	/**
	 * Passes a query.
	 * 
	 * @throws ValidationException
	 *             if data is invalid.
	 */
	public void callBack() throws ValidationException, ConnectorException;

}
