package com.taskadapter.connector.definition;

import java.util.EnumMap;

import com.taskadapter.model.GTaskDescriptor;

/**
 * Builder for "available fields" structure. Something like a workaround for
 * absent "map" syntax in Java. May be used as 
 * <code>builder.addField(FIELD.ID, "id", "a", "b", "c");
 * builder.addField(FIELD.PRIORITY, "p1", "p2", "p3").unselected();
 * builder.addField(FIELD.DUE_DATE, "date1", "date2").withDefault("date2");
 * builder.addField(FIELD.ASSIGNEE, "user1", "user2", "user3").unselected().withDefault("date2");
 */
public final class AvailableFieldsBuilder {
    /**
     * Supported field values. We use "EnumMap" to call a proper constructor in
     * an {@link #end()} method.
     */
    private final EnumMap<GTaskDescriptor.FIELD, FieldConfiguration> fields = new EnumMap<>(
            GTaskDescriptor.FIELD.class);
    
    /**
     * Last added field. Used in field mutators.
     */
    private GTaskDescriptor.FIELD lastAddedField;
    
    private boolean allowSetDefault;
    
    private boolean allowAvailabilitySelection;

    /**
     * We don't need public constructor, bunch of "start" methods is better.
     */
    private AvailableFieldsBuilder() {
        // Just an access controller.
    }

    /**
     * Add a field to "supported fields" list with the list of possible values. Does not allow "redifinition" of a field throwing an
     * {@link IllegalArgumentException}. Field is selected for mapping by default.
     * <p>Default value is set to a first passed value.
     * <p>If no values are specified, then field is added with a one possible
     * value, which equals to a default field description.
     *
     * @param field  field to set possible values for.
     * @param values values to use. e.g. "Due Date" field can be mapped to "Finish date" or "deadline" or something else.
     * @return <code>this</code> builder to allow "chaining" invocation.
     * @throws IllegalArgumentException if any argument is <code>null</code> or there is an attempt
     *                                  to change current mapping.
     */
    public AvailableFieldsBuilder addField(GTaskDescriptor.FIELD field, String... values) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (values == null || values.length == 0) {
            values = new String[]{GTaskDescriptor.getDisplayValue(field)};
        }
        
        final FieldConfiguration current = fields.get(field);
        if (current != null) {
            throw new IllegalArgumentException("Attempt to redefine a field " + field);
        }
        final FieldConfiguration newConfiguration = new FieldConfiguration(
                values, values[0], true);
        fields.put(field, newConfiguration);
        allowSetDefault = true;
        allowAvailabilitySelection = true;
        lastAddedField = field;
        return this;
    }
    
    /**
     * Sets the last added field as "unselected" by default.
     * @return <code>this</code> builder.
     * @throws IllegalStateException if there are no fields at all.
     * @throws IllegalStateException if this field was explicitly set as selected or unselected already.
     */
    public AvailableFieldsBuilder unselected() throws IllegalStateException {
        if (!allowAvailabilitySelection) {
            throw new IllegalStateException("Cannot change default field mapping state");
        }
        allowAvailabilitySelection = false;
        final FieldConfiguration config = fields.get(lastAddedField);
        fields.put(
                lastAddedField,
                new FieldConfiguration(config.getFieldValues(), config
                        .getDefaultValue(), false));
        return this;
    }
    
    /**
     * Sets the default value for the last added field.
     *
     * @param value new default value. Must be one of an allowed field values.
     * @return <code>this</code> builder.
     * @throws IllegalStateException if there are no fields at all.
     * @throws IllegalStateException if defualt field value was explicitly set already. 
     */
    public AvailableFieldsBuilder withDefault(String value) throws IllegalStateException {
        if (!allowSetDefault) {
            throw new IllegalStateException("Cannot set default field value");
        }
        final FieldConfiguration config = fields.get(lastAddedField);
        final FieldConfiguration newConfig = new FieldConfiguration(
                config.getFieldValues(), value, config.isSelectedByDefault());
        allowSetDefault = false;
        fields.put(lastAddedField, newConfig);
        return this;
    }
    
    /**
     * "End" builds and returns an "Available fields" structure. Despite the
     * method name, this method allows "further" building of new fields settings.
     *
     * @return "Available fields" structure.
     */
    public AvailableFields end() {
        return new AvailableFields(new EnumMap<>(fields));
    }

    /**
     * Start building.
     *
     * @return builder for fields.
     */
    public static AvailableFieldsBuilder start() {
        return new AvailableFieldsBuilder();
    }

}
