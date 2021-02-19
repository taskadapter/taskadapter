package com.taskadapter.connector.redmine;

import com.google.common.collect.Lists;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.Field;
import scala.Option;

public class FieldRowFinder {
    public static boolean containsTargetField(Iterable<FieldRow<?>> fieldRows, Field<?> field) {
        return Lists.newArrayList(fieldRows).stream().anyMatch(row -> {
            Option<? extends Field<?>> target = row.targetField();
            return target.isDefined()
                    && target.get().getClass().getName().equals(field.getClass().getName());
        });
    }
}
