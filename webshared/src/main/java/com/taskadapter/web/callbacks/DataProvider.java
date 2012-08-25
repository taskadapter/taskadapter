package com.taskadapter.web.callbacks;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Data provider.
 * 
 * @param <T> type of data.
 */
public interface DataProvider<T> {
    /**
     * Loads data.
     * 
     * @return loaded data.
     * @throws ValidationException if current state is invalid.
     */
    public T loadData() throws ValidationException, ConnectorException;
}
