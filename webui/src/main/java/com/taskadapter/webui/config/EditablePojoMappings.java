package com.taskadapter.webui.config;

import com.google.common.base.Strings;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.config.MappingsValidator;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.Field;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditablePojoMappings {

    private final ConnectorFieldLoader connector1FieldLoader;
    private final ConnectorFieldLoader connector2FieldLoader;

    private List<EditableFieldMapping> editablePojoMappings;

    public EditablePojoMappings(List<FieldMapping<?>> mappings,
                                ConnectorFieldLoader connector1FieldLoader, ConnectorFieldLoader connector2FieldLoader) {
        this.connector1FieldLoader = connector1FieldLoader;
        this.connector2FieldLoader = connector2FieldLoader;

        editablePojoMappings = mappings.stream().map(ro -> {
            var defaultValue = ro.getDefaultValue() == null ? "" : ro.getDefaultValue();

            return new EditableFieldMapping(
                    new Binder<EditableFieldMapping>(EditableFieldMapping.class),
                    UUID.randomUUID().toString(),
                    ro.getFieldInConnector1().map(Field::name).orElse(""),
                    ro.getFieldInConnector2().map(Field::name).orElse(""),
                    ro.isSelected(),
                    defaultValue
            );
        }).collect(Collectors.toList());
    }


    void removeFieldFromList(EditableFieldMapping field) {
        editablePojoMappings = editablePojoMappings.stream().filter(
                e -> !(e.getUniqueIdForTemporaryMap().equals(field.getUniqueIdForTemporaryMap()))
        ).collect(Collectors.toList());
    }

    public void validate() throws BadConfigException {
        MappingsValidator.validate(editablePojoMappings);
    }

    public List<FieldMapping<?>> getElements() {
        Stream<FieldMapping<?>> fieldMappingStream = editablePojoMappings.stream()
                // skip empty rows
                .filter(e ->
                        !(Strings.isNullOrEmpty(e.getFieldInConnector1()) && Strings.isNullOrEmpty(e.getFieldInConnector2())))
                // convert to the output format
                .map(e ->
                        new FieldMapping<>(
                                getField(e.getFieldInConnector1(), connector1FieldLoader),
                                getField(e.getFieldInConnector2(), connector2FieldLoader),
                                e.getSelected(),
                                e.getDefaultValue()
                        )
                );
        return fieldMappingStream.collect(Collectors.toList());
    }

    <T> Optional<Field<T>> getField(String fieldName, ConnectorFieldLoader fieldLoader) {
        if (Strings.isNullOrEmpty(fieldName)) {
            return Optional.empty();
        } else {
            var field = fieldLoader.getTypeForFieldName(fieldName);
            return Optional.of((Field<T>) field);
        }
    }

    void add(EditableFieldMapping m) {
        editablePojoMappings.add(m);
    }

    public List<EditableFieldMapping> getEditablePojoMappings() {
        return editablePojoMappings;
    }
}