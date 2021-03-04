package com.taskadapter.reporting;

import com.google.common.base.Strings;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.common.FieldPrettyNameBuilder;
import com.taskadapter.model.Field;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FieldMappingFormatter {
    public static String format(List<FieldMapping<?>> mappings) {
        return mappings.stream().map(m -> {
            var field1 = formatField(m.getFieldInConnector1());
            var field2 = formatField(m.getFieldInConnector2());
            return String.format("%1$s - %2$s selected: %3$s default: %4$s",
                    field1, field2, m.isSelected(), m.getDefaultValue());
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    private static String formatField(Optional<? extends Field<?>> field) {
        var string = field.map(FieldPrettyNameBuilder::getPrettyFieldName).orElse("None");
        return Strings.padEnd(string, 30, ' ');
    }
}
