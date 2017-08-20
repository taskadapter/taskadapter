package com.taskadapter.connector.definition;

import java.util.Arrays;

/**
 * Describes one field in field mappings.
 * 
 * @author maxkar
 * 
 */
final class FieldConfiguration {

    /**
     * Supported fields values.
     */
    private final String[] fieldValues;

    /**
     * Default field value. Must be one of <code>fieldValues</code>
     */
    private final String defaultValue;
    
    /**
     * "Selected by default" field flag.
     */
    private final boolean selectedByDefault;

    /**
     * Creates a new fields configuration.
     * 
     * @param fieldValues
     *            allowed field values.
     * @param defaultValue
     *            default fileld value.
     */
    public FieldConfiguration(String[] fieldValues, String defaultValue, boolean selectedByDefault) {
        if (fieldValues == null) {
            throw new IllegalArgumentException("Field values can't be null");
        }
        if (defaultValue == null) {
            throw new IllegalArgumentException("Default value cannot be null");
        }
        if (Arrays.asList(fieldValues).indexOf(defaultValue) < 0) {
            throw new IllegalArgumentException("Default value " + defaultValue
                    + " is not present in " + Arrays.toString(fieldValues));
        }
        this.fieldValues = fieldValues.clone();
        this.defaultValue = defaultValue;
        this.selectedByDefault = selectedByDefault;
    }

    /**
     * Returns field values.
     * 
     * @return available field values.
     */
    String[] getFieldValues() {
        return fieldValues.clone();
    }

    /**
     * Returns a field default value. It is guaranteed that defaultValues is
     * always exists in <code>getFieldValues()</code>.
     * 
     * @return field default value.
     */
    String getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Returns a "selected by default" flag.
     * @return "selected by default" flag.
     */
    public boolean isSelectedByDefault() {
        return selectedByDefault;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + Arrays.hashCode(fieldValues);
        result = prime * result + (selectedByDefault ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FieldConfiguration other = (FieldConfiguration) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (!Arrays.equals(fieldValues, other.fieldValues))
            return false;
        if (selectedByDefault != other.selectedByDefault)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "FieldConfiguration [fieldValues="
                + Arrays.toString(fieldValues) + ", defaultValue="
                + defaultValue + ", selectedByDefault=" + selectedByDefault
                + "]";
    }

}
