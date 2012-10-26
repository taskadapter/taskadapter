package com.taskadapter.connector.definition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

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
	private final Map<GTaskDescriptor.FIELD, FieldConfiguration> fieldValues;

	/**
	 * Creates a new "available fields" settings.
	 * 
	 * @param fieldValues
	 *            supported field values.
	 */
	AvailableFields(Map<FIELD, FieldConfiguration> fieldValues) {
		this.fieldValues = fieldValues;
	}

	public String[] getAllowedValues(FIELD field) {
		final FieldConfiguration guess = fieldValues.get(field);
		return guess != null ? guess.getFieldValues() : EMPTY_VALUES;
	}
	
	public String getDefaultValue(FIELD field) {
        final FieldConfiguration guess = fieldValues.get(field);
        return guess != null ? guess.getDefaultValue() : "";
	}
	
	public boolean isSelectedByDefault(FIELD field) {
        final FieldConfiguration guess = fieldValues.get(field);
        return guess != null && guess.isSelectedByDefault();
	}

    public boolean isFieldSupported(FIELD field) {
        return fieldValues.containsKey(field);
    }

    /**
     * Returns collection of supported fields.
     * @return collection of supported fields.
     */
    public Collection<FIELD> getSupportedFields() {
        return Collections.unmodifiableCollection(fieldValues.keySet());
    }

}
