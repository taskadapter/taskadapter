package com.taskadapter.connector.msp;

import com.taskadapter.model.GTaskDescriptor;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DefaultMSPMappingsTest {
    @Test
    public void summaryFieldSelectedByDefault() {
        assertTrue(DefaultMSPMappings.generate().isFieldSelected(GTaskDescriptor.FIELD.SUMMARY));
    }

}
