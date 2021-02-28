package com.taskadapter.connector.common;

import com.taskadapter.model.Field;

public class FieldPrettyNameBuilder {
    public static String getPrettyFieldName(Field<?> f) {
        if (f.getClass().getSimpleName().startsWith("Custom")) {
            return f.toString();
        }
        return f.getClass().getSimpleName().replace("$", "");
    }
}
