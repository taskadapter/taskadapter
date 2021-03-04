package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.Field;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FieldRowBuilder {
    public static List<FieldRow<?>> rows(List<Field<?>> fields) {
        return fields.stream()
                .map(FieldRowBuilder::build)
                .collect(Collectors.toList());
    }

    private static <T> FieldRow<T> build(Field<T> f) {
        return new FieldRow<T>(Optional.of(f), Optional.of(f), null);
    }
}
