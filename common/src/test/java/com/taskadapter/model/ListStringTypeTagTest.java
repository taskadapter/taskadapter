package com.taskadapter.model;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListStringTypeTagTest {
    @Test
    public void canParse() {
        assertThat(
                new ListStringTypeTag().fromString("a b c")
        ).isEqualTo(List.of("a", "b", "c"));
    }

    @Test
    public void parsesNullToNull() {
        assertThat(
                new ListStringTypeTag().fromString(null)
        ).isNull();
    }
}
