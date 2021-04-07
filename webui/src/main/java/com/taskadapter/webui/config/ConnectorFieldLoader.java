package com.taskadapter.webui.config;

import com.taskadapter.model.CustomString;
import com.taskadapter.model.Field;

import java.util.List;

public class ConnectorFieldLoader {
    private final List<Field<?>> fields;

    public ConnectorFieldLoader(List<Field<?>> fields) {
        this.fields = fields;
    }

    Field<?> getTypeForFieldName(String fieldName) {
        return fields.stream()
                .filter(f -> f.getFieldName().equals(fieldName))
                .findFirst()
                .orElse(new CustomString(fieldName));
    }
}
