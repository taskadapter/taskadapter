package com.taskadapter.connector.definition;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class NewMappingsTest {
    @Test
    public void nullReturnedForNullParameter() {
        NewMappings mappings = new NewMappings();
        assertNull(mappings.getMapping(null));
    }}
