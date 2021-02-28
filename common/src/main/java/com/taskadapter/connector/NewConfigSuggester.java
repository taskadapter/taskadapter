package com.taskadapter.connector;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.model.Field;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Given two maps of available connector-specific fields (for connector 1 and connector 2),
 * build a list of {@link com.taskadapter.connector.definition.FieldMapping} with suggested mappings: what fields in connector 1
 * are equivalent to fields in connector 2.
 * Equivalent means they refer to the same {@link com.taskadapter.model.Field}.
 */
public class NewConfigSuggester {
    /**
     * try to match list of fields for connector 1 with the list for connector 2.
     */
    public static List<FieldMapping<?>> suggestedFieldMappingsForNewConfig(List<Field<?>> list1, List<Field<?>> list2) {
        var listIntersection = list1.stream().filter(list2::contains).collect(Collectors.toList());
        return listIntersection.stream().map(NewConfigSuggester::duplicateFieldIntoMapping)
                .collect(Collectors.toList());
    }

    public static <T> FieldMapping<T> duplicateFieldIntoMapping(Field<T> field) {
        return FieldMapping.apply(field, field, true, null);
    }
}
