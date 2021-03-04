package com.taskadapter.common.ui;

import com.taskadapter.model.Field;

import java.util.Objects;
import java.util.Optional;

public class FieldMapping<T> {
    private Field<T> fieldInConnector1;
    private Field<T> fieldInConnector2;
    private boolean selected;
    private String defaultValue;

    public FieldMapping() {
    }

    public FieldMapping(Optional<Field<T>> fieldInConnector1, Optional<Field<T>> fieldInConnector2, boolean selected, String defaultValue) {
        this.fieldInConnector1 = fieldInConnector1.orElse(null);
        this.fieldInConnector2 = fieldInConnector2.orElse(null);
        this.selected = selected;
        this.defaultValue = defaultValue;
    }

    public FieldMapping(Field<T> fieldInConnector1, Field<T> fieldInConnector2, boolean selected, String defaultValue) {
        this.fieldInConnector1 = fieldInConnector1;
        this.fieldInConnector2 = fieldInConnector2;
        this.selected = selected;
        this.defaultValue = defaultValue;
    }


    public static FieldMapping apply(Field<?> fieldInConnector1, Field<?> fieldInConnector2, boolean selected,
                                     String defaultValue) {
        return new FieldMapping(fieldInConnector1, fieldInConnector2, selected, defaultValue);
    }

    public Optional<Field<T>> getFieldInConnector1() {
        return Optional.ofNullable(fieldInConnector1);
    }

    public void setFieldInConnector1(Optional<Field<T>> fieldInConnector1) {
        this.fieldInConnector1 = fieldInConnector1.orElse(null);
    }

    public Optional<Field<T>> getFieldInConnector2() {
        return Optional.ofNullable(fieldInConnector2);
    }

    public void setFieldInConnector2(Optional<Field<T>> fieldInConnector2) {
        this.fieldInConnector2 = fieldInConnector2.orElse(null);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldMapping<?> that = (FieldMapping<?>) o;
        return selected == that.selected && Objects.equals(fieldInConnector1, that.fieldInConnector1) && Objects.equals(fieldInConnector2, that.fieldInConnector2) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldInConnector1, fieldInConnector2, selected, defaultValue);
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "fieldInConnector1=" + fieldInConnector1 +
                ", fieldInConnector2=" + fieldInConnector2 +
                ", selected=" + selected +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
