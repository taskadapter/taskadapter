package com.taskadapter.connector.common;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * When saving a task, we need to set some of its fields to some default value if there is nothing there yet.
 * E.g. "environment" field can be set as required in Jira and then saving data to Jira will fail
 * if the source data does not contain that info. To fix this, we have "default value if empty" column on
 * "Task Fields Mapping" panel.
 * <p>
 * This class sets those default values to empty fields.
 */
public class DefaultValueSetter {
    /**
     * Format for dates in "default value if empty " fields on "Task Fields Mapping" panel.
     */
    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy MM dd");

    private final Mappings mappings;

    public DefaultValueSetter(Mappings mappings) {
        this.mappings = mappings;
    }

    public GTask cloneAndReplaceEmptySelectedFieldsWithDefaultValues(GTask task) throws ParseException {
        GTask result = new GTask(task);
        for (GTaskDescriptor.FIELD selectedField : mappings.getSelectedFields()) {
            Object currentFieldValue = task.getValue(selectedField);
            if (fieldIsConsideredEmpty(currentFieldValue)) {
                String defaultValueForEmptyField = mappings.getDefaultValueForEmptyField(selectedField);
                Object valueWithProperType = getValueWithProperType(selectedField, defaultValueForEmptyField);
                result.setValue(selectedField, valueWithProperType);
            }
        }
        return result;
    }

    private boolean fieldIsConsideredEmpty(Object value) {
        return (value == null)
                || ((value instanceof String) && ((String) value).isEmpty());
    }

    private Object getValueWithProperType(GTaskDescriptor.FIELD field, String value) throws ParseException {
        Object objectToSet;
        switch (field) {
            case ASSIGNEE:
                objectToSet = new GUser(value);
                break;
            case START_DATE:
                objectToSet = parseDate(value);
                break;
            case DUE_DATE:
                objectToSet = parseDate(value);
                break;
            case ESTIMATED_TIME:
                objectToSet = parseFloat(value);
                break;
            default:
                // assign the string by default
                objectToSet = value;
                break;
        }
        return objectToSet;
    }

    private Float parseFloat(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return Float.parseFloat(value);
    }

    private Date parseDate(String value) throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return DATE_PARSER.parse(value);
    }
}
