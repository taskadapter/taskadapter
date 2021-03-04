package com.taskadapter.connector.testlib;

import com.taskadapter.model.Field;

public class FieldWithValue<T> {
    Field<T> field;
    T value;

    public FieldWithValue(Field<T> field, T value) {
        this.field = field;
        this.value = value;
    }
}
