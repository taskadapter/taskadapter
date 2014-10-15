package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MappingsTest {
    @Test
    public void unselectedMappingCopiedProperlyByCopyConstructor() {
        Mappings mappings = new Mappings();
        mappings.setMapping(GTaskDescriptor.FIELD.ESTIMATED_TIME, true, "mapToSomething", "some default value here");
        assertTrue(mappings.isFieldSelected(GTaskDescriptor.FIELD.ESTIMATED_TIME));
        mappings.setMapping(GTaskDescriptor.FIELD.ESTIMATED_TIME, false, "mapToSomething", "some default value here");
        Mappings cloned = new Mappings(mappings);
        assertFalse(cloned.isFieldSelected(GTaskDescriptor.FIELD.ESTIMATED_TIME));
    }

    @Test
    public void mappingCopiedProperlyByCopyConstructor() {
        Mappings mappings = new Mappings();
        mappings.setMapping(GTaskDescriptor.FIELD.ESTIMATED_TIME, true, "mapToSomething", "some default value here");
        Mappings cloned = new Mappings(mappings);
        assertTrue(cloned.isFieldSelected(GTaskDescriptor.FIELD.ESTIMATED_TIME));
        assertEquals("mapToSomething", cloned.getMappedTo(GTaskDescriptor.FIELD.ESTIMATED_TIME));
    }
}
