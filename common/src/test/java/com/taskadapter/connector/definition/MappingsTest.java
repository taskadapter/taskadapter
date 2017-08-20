package com.taskadapter.connector.definition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MappingsTest {
    @Test
    public void unselectedMappingCopiedProperlyByCopyConstructor() {
        Mappings mappings = new Mappings();
        mappings.setMapping("estimatedTime", true, "mapToSomething", "some default value here");
        assertTrue(mappings.isFieldSelected("estimatedTime"));
        mappings.setMapping("estimatedTime", false, "mapToSomething", "some default value here");
        Mappings cloned = new Mappings(mappings);
        assertFalse(cloned.isFieldSelected("estimatedTime"));
    }

    @Test
    public void mappingCopiedProperlyByCopyConstructor() {
        Mappings mappings = new Mappings();
        mappings.setMapping("estimatedTime", true, "mapToSomething", "some default value here");
        Mappings cloned = new Mappings(mappings);
        assertTrue(cloned.isFieldSelected("estimatedTime"));
        assertEquals("mapToSomething", cloned.getMappedTo("estimatedTime"));
    }
}
