package com.taskadapter.connector.redmine;

import com.taskadapter.model.Field;
import com.taskadapter.redmineapi.bean.CustomFieldDefinition;

import java.util.List;

public class CustomFieldDefinitionFinder {
    public static Integer findCustomFieldId(List<CustomFieldDefinition> customFieldDefinitions, Field<?> field) {
        return customFieldDefinitions.stream()
                .filter(d -> d.getName().equals(field.getFieldName()))
                .findFirst().map(CustomFieldDefinition::getId)
                .orElse(null);
    }
}
