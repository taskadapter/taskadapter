package com.taskadapter.connector;

import com.taskadapter.connector.definition.ExportDirection;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.model.Field;
import scala.Option;

import java.util.List;
import java.util.stream.Collectors;

public class MappingBuilder {
    public static List<FieldRow<?>> build(List<FieldMapping<?>> newMappings, ExportDirection exportDirection) {
        return newMappings.stream()
                .filter(FieldMapping::selected)
                .map(mapping -> buildRow(mapping, exportDirection))
                .collect(Collectors.toList());
    }

    private static <T> FieldRow<T> buildRow(FieldMapping<T> mapping, ExportDirection exportDirection) {
        return new FieldRow<>(
                getSourceField(mapping, exportDirection),
                getTargetField(mapping, exportDirection),
                mapping.defaultValue());
    }

    public static <T> Option<Field<T>> getSourceField(FieldMapping<T> fieldMapping, ExportDirection exportDirection) {
        return switch (exportDirection) {
            case RIGHT -> fieldMapping.fieldInConnector1();
            case LEFT -> fieldMapping.fieldInConnector2();
        };
    }

    public static <T> Option<Field<T>> getTargetField(FieldMapping<T> fieldMapping, ExportDirection exportDirection) {
        return switch (exportDirection) {
            case RIGHT -> fieldMapping.fieldInConnector2();
            case LEFT -> fieldMapping.fieldInConnector1();
        };
    }
}
