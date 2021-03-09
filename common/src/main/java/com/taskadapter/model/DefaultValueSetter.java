package com.taskadapter.model;

import com.taskadapter.connector.FieldRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * When saving a task, we need to set some of its fields to some default value if there is nothing there yet.
 * E.g. "environment" field can be set as required in Jira and then saving data to Jira will fail
 * if the source data does not contain that info. To fix this, we have "default value if empty" column on
 * "Task Fields Mapping" panel.
 * <p>
 * This class sets those default values to empty fields.
 */
public class DefaultValueSetter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultValueSetter.class);

    public static GTask adapt(Iterable<FieldRow<?>> fieldRows, GTask task) {
        var result = new GTask();
        fieldRows.forEach(row -> adaptRow(task, result, row));
        result.setSourceSystemId(task.getSourceSystemId());
        result.setParentIdentity(task.getParentIdentity());
        result.setChildren(task.getChildren());
        return result;
    }

    private static <T> void adaptRow(GTask task, GTask result, FieldRow<T> row) {
        if (row.getTargetField().isPresent()) {
            var fieldToLoadValueFrom = row.getSourceField();
            var currentFieldValue = fieldToLoadValueFrom.map(task::getValue);

            T newValue;

            if (fieldIsConsideredEmpty(currentFieldValue)) {
                // use a fake string field if no field exists for the source side. value will come from "default" then.
                var field = fieldToLoadValueFrom.orElse((Field<T>) new CustomString("dummy"));
                if (field instanceof CanBeLoadedFromString) {
                    newValue = ((CanBeLoadedFromString<T>) field).getStringValueParser()
                            .fromString(row.getDefaultValueForEmpty());
                } else {
                    logger.error("DefaultValueSetter: ignoring field type " + fieldToLoadValueFrom +
                            " - it does not support parsing values from a string");
                    newValue = null;
                }
            } else {
                newValue = currentFieldValue.get();
            }

            var targetField = row.getTargetField().get();
            result.setValue(targetField, newValue);
        }
    }

    private static boolean fieldIsConsideredEmpty(Optional<?> value) {
        return value.isEmpty()
                || (value.get() instanceof String && ((String) value.get()).isEmpty());
    }
}
