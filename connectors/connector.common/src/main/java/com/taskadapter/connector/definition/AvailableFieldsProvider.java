package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

import java.util.Collection;

public interface AvailableFieldsProvider {
	String[] getAllowedValues(GTaskDescriptor.FIELD field);
	
	/**
	 * @return collection of Task Adapter fields supported by a connector. 
	 *         E.g. Github may not support "estimated time" or whatever.
	 */
	Collection<GTaskDescriptor.FIELD> getSupportedFields();
}
