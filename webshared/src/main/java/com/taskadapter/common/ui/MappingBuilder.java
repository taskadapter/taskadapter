package com.taskadapter.common.ui;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.ExportDirection;
import com.taskadapter.model.Field;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MappingBuilder {
    public static List<FieldRow<?>> build(List<FieldMapping<?>> newMappings, ExportDirection exportDirection) {
        return newMappings.stream()
                .filter(FieldMapping::isSelected)
                .map(mapping -> buildRow(mapping, exportDirection))
                .collect(Collectors.toList());
    }

    private static <T> FieldRow<T> buildRow(FieldMapping<T> mapping, ExportDirection exportDirection) {
        return new FieldRow<>(
                getSourceField(mapping, exportDirection),
                getTargetField(mapping, exportDirection),
                mapping.getDefaultValue());
    }

    public static <T> Optional<Field<T>> getSourceField(FieldMapping<T> fieldMapping, ExportDirection exportDirection) {
        return switch (exportDirection) {
            case RIGHT -> fieldMapping.getFieldInConnector1();
            case LEFT -> fieldMapping.getFieldInConnector2();
        };
    }

    public static <T> Optional<Field<T>> getTargetField(FieldMapping<T> fieldMapping, ExportDirection exportDirection) {
        return switch (exportDirection) {
            case RIGHT -> fieldMapping.getFieldInConnector2();
            case LEFT -> fieldMapping.getFieldInConnector1();
        };
    }
}
