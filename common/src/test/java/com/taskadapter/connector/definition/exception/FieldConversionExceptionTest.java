package com.taskadapter.connector.definition.exception;

import com.taskadapter.model.AllFields;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldConversionExceptionTest {
    @Test
    public void emptyCollection() {
        assertThat(
                new FieldConversionException("JIRA", AllFields.summary, List.of(), "").getMessage())
                .contains("Empty collection cannot");
    }

    @Test
    public void collectionWithItems() {
        assertThat(
                new FieldConversionException("JIRA", AllFields.summary, List.of("component1", "component2"), "").getMessage())
                .contains("Collection of (component1,component2) cannot");
    }

    @Test
    public void integerValue() {
        assertThat(new FieldConversionException("JIRA", AllFields.summary, 123, "").getMessage())
                .contains("Value '123' cannot");
    }
}
