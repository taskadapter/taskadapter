package com.taskadapter.web.callbacks;

import com.taskadapter.connector.definition.ValidationException;

/**
 * Data callback.
 * 
 * @author maxkar
 * 
 * @param <T>
 *            type of data to receive in callback.
 */
public interface DataCallback<T> {
	/**
	 * Passes a data ack.
	 * 
	 * @param data
	 *            data to receive.
	 * @throws ValidationException
	 *             if data is invalid.
	 */
	public void callBack(T data) throws ValidationException;
}
