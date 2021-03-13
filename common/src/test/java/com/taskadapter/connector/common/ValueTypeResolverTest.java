package com.taskadapter.connector.common;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueTypeResolverTest {
    @Test
    public void nullToEmpty() {
        assertThat(ValueTypeResolver.getValueAsString(null))
                .isEqualTo("");
    }

    @Test
    public void stringToString() {
        assertThat(ValueTypeResolver.getValueAsString("abc")).isEqualTo("abc");
    }

    @Test
    public void emptyListToEmpty() {
        assertThat(ValueTypeResolver.getValueAsString(List.of()))
                .isEqualTo("");
    }

    @Test
    public void listOfStringsToFirstString() {
        assertThat(ValueTypeResolver.getValueAsString(List.of("a", "b")))
                .isEqualTo("a");
    }
}
