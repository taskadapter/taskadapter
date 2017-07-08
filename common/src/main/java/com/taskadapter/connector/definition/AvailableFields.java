package com.taskadapter.connector.definition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Available fields settings. Provides information about "supported" fields and
 * supported field values. Use {@link AvailableFieldsBuilder} to create instances of this class.
 */
public final class AvailableFields {
	
	/**
	 * "Empty" string set.
	 */
	private static final String[] EMPTY_VALUES = new String[0];

	/**
	 * Map of supported field values.
	 */
	private final Map<String, FieldConfiguration> fieldValues;

	/**
	 * Creates a new "available fields" settings.
	 * 
	 * @param fieldValues
	 *            supported field values.
	 */
	AvailableFields(Map<String, FieldConfiguration> fieldValues) {
		this.fieldValues = fieldValues;
	}

	public String[] getAllowedValues(String field) {
		final FieldConfiguration guess = fieldValues.get(field);
		return guess != null ? guess.getFieldValues() : EMPTY_VALUES;
	}
	
	public String getDefaultValue(String field) {
        final FieldConfiguration guess = fieldValues.get(field);
        return guess != null ? guess.getDefaultValue() : "";
	}
	
	public boolean isSelectedByDefault(String field) {
        final FieldConfiguration guess = fieldValues.get(field);
        return guess != null && guess.isSelectedByDefault();
	}

    public boolean isFieldSupported(String field) {
        return fieldValues.containsKey(field);
    }

    public Collection<String> getSupportedFields() {
        return Collections.unmodifiableCollection(fieldValues.keySet());
    }

    public Map<String, FieldConfiguration> getFields() {
        return fieldValues;
    }

}
