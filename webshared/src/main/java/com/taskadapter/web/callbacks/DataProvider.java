package com.taskadapter.web.callbacks;

import com.taskadapter.connector.definition.ValidationException;

/**
 * Data provider.
 * 
 * @author maxkar
 * 
 * @param <T>
 *            type of data.
 */
public interface DataProvider<T> {
	/**
	 * Loads a data.
	 * 
	 * @return loaded data.
	 * @throws ValidationException
	 *             if current state is invalid.
	 */
	public T loadData() throws ValidationException;
}
