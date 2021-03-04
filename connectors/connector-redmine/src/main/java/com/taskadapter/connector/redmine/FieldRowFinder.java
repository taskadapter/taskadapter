package com.taskadapter.connector.redmine;

import com.google.common.collect.Lists;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.Field;

import java.util.Optional;

public class FieldRowFinder {
    // TODO 14 move to common?
    public static boolean containsTargetField(Iterable<FieldRow<?>> fieldRows, Field<?> field) {
        return Lists.newArrayList(fieldRows).stream().anyMatch(row -> {
            Optional<? extends Field<?>> target = row.targetField();
            return target.isPresent()
                    && target.get().getClass().getName().equals(field.getClass().getName());
        });
    }
}
