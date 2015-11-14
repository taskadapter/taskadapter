package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTaskDescriptor;

/**
 * Output context for a JSON writing.
 * 
 */
public interface OutputContext {
    /**
     * Returns a JSON name for a gtask field. If field should not be set,
     * returns null.
     * 
     * @param field
     *            field to set.
     * @return json name for a field or <code>null</code>.
     * @throws ConnectorException
     */
    String getJsonName(GTaskDescriptor.FIELD field)
            throws ConnectorException;
}
