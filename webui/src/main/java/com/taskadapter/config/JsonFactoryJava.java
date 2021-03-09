package com.taskadapter.config;

import com.taskadapter.model.Field;

import java.util.Optional;

public class JsonFactoryJava {
    static String optionalToString(Optional<? extends Field> f) {
        if (f.isEmpty()) {
            return "null";
        } else {
            var typeName = f.get().getClass().getSimpleName();
            var value = f.get().getFieldName();
            return "{ \"type\" : \"" + typeName + "\", \"name\": \"" + value + "\" }";
        }
    }
}
