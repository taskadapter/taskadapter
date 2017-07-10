package com.taskadapter.web.uiapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UIConfigStoreTest {
    // TODO TA3 these tests won't work without MSP editor dependency

    /*
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void configCreatedWithProperDefaultMappings() throws Exception {
        final UIConfigStore store = TestUIConfigStoreFactory.createStore(tempFolder.getRoot());
        UISyncConfig config = store.createNewConfig("admin", "label1", "Redmine REST", "Microsoft Project");
        checkFieldSelected(config.getNewMappings(), "Start Date", "MUST_START_ON");
    }

    private void checkFieldSelected(NewMappings newMappings, String connector1ExpectedValue, String connector2ExpectedValue) {
        FieldMapping fieldMapping = findField(newMappings.getMappingsString(), GTaskDescriptor.FIELD.START_DATE);
        assertEquals(connector1ExpectedValue, fieldMapping.getConnector1());
        assertEquals(connector2ExpectedValue, fieldMapping.getConnector2());
        assertTrue(fieldMapping.isSelected());
    }

    private FieldMapping findField(Collection<FieldMapping> mappings, GTaskDescriptor.FIELD field) {
        for (FieldMapping mapping : mappings) {
            if (mapping.getField().equals(field)) {
                return mapping;
            }
        }
        return null;
    }*/
}
