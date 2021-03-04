package com.taskadapter.connector.common;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.DefaultValueResolver;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;

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
    public static GTask adapt(Iterable<FieldRow<?>> fieldRows, GTask task) {
        var result = new GTask();
        fieldRows.forEach(row -> adaptRow(task, result, row));
        result.setSourceSystemId(task.getSourceSystemId());
        result.setParentIdentity(task.getParentIdentity());
        result.setChildren(task.getChildren());
        return result;
    }

    private static <T> void adaptRow(GTask task, GTask result, FieldRow<T> row) {
        if (row.targetField().isPresent()) {
            var fieldToLoadValueFrom = row.sourceField();
            var currentFieldValue = fieldToLoadValueFrom.map(task::getValue);

            var newValue =
                    fieldIsConsideredEmpty(currentFieldValue) ?
                            (T) getValueWithProperType(
                                    // use a fake string field if no field exists for the source side. value will come from "default" then.
                                    fieldToLoadValueFrom.orElse((Field<T>) Field.apply("dummy")),
                                    row.defaultValueForEmpty())
                            : currentFieldValue.get();

            var targetField = row.targetField().get();
            result.setValue(targetField, newValue);
        }
    }

    private static boolean fieldIsConsideredEmpty(Optional<?> value) {
        return value.isEmpty()
                || (value.get() instanceof String && ((String) value.get()).isEmpty());
    }

    private static Object getValueWithProperType(Field<?> field, String value) {
        return DefaultValueResolver.getTag(field).parseDefault(value);
    }
}
