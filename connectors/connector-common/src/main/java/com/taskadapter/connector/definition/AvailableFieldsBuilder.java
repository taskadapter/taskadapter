package com.taskadapter.connector.definition;

import java.util.Arrays;
import java.util.EnumMap;

import com.taskadapter.model.GTaskDescriptor;

/**
 * Builder for "available fields" structure. Something like a workaround for
 * absent "map" syntax in Java.
 */
public final class AvailableFieldsBuilder {
    /**
     * Supported field values. We use "EnumMap" to call a proper constructor in
     * an {@link #end()} method.
     */
    private final EnumMap<GTaskDescriptor.FIELD, String[]> fields = new EnumMap<GTaskDescriptor.FIELD, String[]>(
            GTaskDescriptor.FIELD.class);

    /**
     * We don't need public constructor, bunch of "start" methods is better.
     */
    private AvailableFieldsBuilder() {
        // Just an access controller.
    }

    /**
     * Add a field to "supported fields" list with the list of possible values. Does not allow "redifinition" of a field throwing an
     * {@link IllegalArgumentException}.
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
        if (values == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }
        final String[] current = fields.get(field);
        if (current != null && !Arrays.equals(current, values)) {
            throw new IllegalArgumentException(
                    "Attempt to change set of supported values for field " + field + ", current values " + Arrays.toString(current) + ", new values " + Arrays.toString(values));
        }
        fields.put(field, values);
        return this;
    }

    /**
     * "Ends" builds and returns an "Available fields" structure. Despite
     * method name, this method allows "further" building of new fields
     * settings.
     *
     * @return "Available fields" structure.
     */
    public AvailableFields end() {
        return new AvailableFields(new EnumMap<GTaskDescriptor.FIELD, String[]>(fields));
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
