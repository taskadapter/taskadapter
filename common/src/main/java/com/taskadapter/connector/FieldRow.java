package com.taskadapter.connector;

import com.taskadapter.model.Field;

import java.util.Optional;

public class FieldRow<T> {
    private Optional<Field<T>> sourceField;
    private Optional<Field<T>> targetField;
    private String defaultValueForEmpty;

    public FieldRow(Optional<Field<T>> sourceField, Optional<Field<T>> targetField, String defaultValueForEmpty) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.defaultValueForEmpty = defaultValueForEmpty;
    }

    public static <T> FieldRow<T> apply(Field<T> sourceField, Field<T> targetField, String defaultValueForEmpty) {
        return new FieldRow(Optional.ofNullable(sourceField), Optional.ofNullable(targetField), defaultValueForEmpty);
    }

    public Optional<Field<T>> getSourceField() {
        return sourceField;
    }

    public Optional<Field<T>> getTargetField() {
        return targetField;
    }

    public String getDefaultValueForEmpty() {
        return defaultValueForEmpty;
    }
}
