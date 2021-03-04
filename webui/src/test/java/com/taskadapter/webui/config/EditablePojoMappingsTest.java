package com.taskadapter.webui.config;

import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Field;
import com.vaadin.flow.data.binder.Binder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EditablePojoMappingsTest {

    private static final ConnectorFieldLoader connector1FieldLoader = new ConnectorFieldLoader(List.of(
            CustomString.apply("field 1"),
            CustomString.apply("field 2"),
            CustomString.apply("summary"),
            CustomString.apply("another")
    ));

    private static final ConnectorFieldLoader connector2FieldLoader = connector1FieldLoader;

    @Test
    public void returnsEmptyFieldNameOnTheLeftAsOptionalEmpty() {
        var mappings = new EditablePojoMappings(Arrays.asList(new FieldMapping(Field.apply("field 1"), Field.apply("field 2"), true, "default")), connector1FieldLoader, connector2FieldLoader);
        mappings.getEditablePojoMappings().get(0).setFieldInConnector1("");

        assertThat(mappings.getElements()).containsOnly(new FieldMapping(Optional.empty(), Optional.of(Field.apply("field 2")), true, "default"));
    }

    @Test
    public void returnsEmptyFieldNameOnTheRightAsOptionalEmpty() {
        var mappings = new EditablePojoMappings(Arrays.asList(new FieldMapping(Optional.of(Field.apply("field 1")), Optional.of(Field.apply("field 2")), true, "default")), connector1FieldLoader, connector2FieldLoader);
        mappings.getEditablePojoMappings().get(0).setFieldInConnector2("");

        assertThat(mappings.getElements()).containsOnly(new FieldMapping(Optional.of(Field.apply("field 1")), Optional.empty(), true, "default"));
    }

    // Vaadin sets NULL as field value when you select an "empty" element in ListSelect
    @Test
    public void fieldClearedWithNullBecomesOptionalEmpty() {
        var mappings = new EditablePojoMappings(Arrays.asList(new FieldMapping(Optional.of(Field.apply("field 1")), Optional.of(Field.apply("date 1")), true, "default")), connector1FieldLoader, connector2FieldLoader);
        mappings.getEditablePojoMappings().get(0).setFieldInConnector2(null);

        assertThat(mappings.getElements()).containsOnly(new FieldMapping(Optional.of(Field.apply("field 1")), Optional.empty(), true, "default"));
    }

    @Test
    public void returnsNewField() {
        var mappings = new EditablePojoMappings(Arrays.asList(), connector1FieldLoader, connector2FieldLoader);
        mappings.add(new EditableFieldMapping(
                createBinder(),
                "123", "", "summary", true, "default"));
        assertThat(mappings.getElements()).containsOnly(new FieldMapping(Optional.empty(), Optional.of(Field.apply("summary")), true, "default"));
    }

    @Test
    public void skipsEmptyRows() {
        var mappings = new EditablePojoMappings(Arrays.asList(), connector1FieldLoader, connector2FieldLoader);
        mappings.add(new EditableFieldMapping(createBinder(), "100", "", "summary", true, "default"));
        mappings.add(new EditableFieldMapping(createBinder(), "200", "field 1", "", true, "default"));
        mappings.add(new EditableFieldMapping(createBinder(), "300", "", "", true, "default"));
        mappings.add(new EditableFieldMapping(createBinder(), "400", "", "another", true, "default"));
        assertThat(mappings.getElements()).containsOnly(
                new FieldMapping(Optional.empty(), Optional.of(Field.apply("summary")), true, "default"),
                new FieldMapping(Optional.of(Field.apply("field 1")), Optional.empty(), true, "default"),
                new FieldMapping(Optional.empty(), Optional.of(Field.apply("another")), true, "default")
        );
    }

    private static Binder<EditableFieldMapping> createBinder() {
        return new Binder<>(EditableFieldMapping.class);
    }
}
