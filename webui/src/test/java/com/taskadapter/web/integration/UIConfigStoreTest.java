package com.taskadapter.web.integration;

import com.taskadapter.FileManager;
import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.web.uiapi.UISyncConfig;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UIConfigStoreTest extends FileBasedTest {
    @Test
    public void configCreatedWithProperDefaultMappings() throws Exception {
        FileManager fileManager = new FileManager(tempFolder);
        final ConfigStorage configStorage = new ConfigStorage(fileManager);

        EditorManager editorManager = EditorManager.fromResource("editors.txt");
        UIConfigService uiConfigService = new UIConfigService(new PluginManager(), editorManager);

        UIConfigStore store = new UIConfigStore(uiConfigService, configStorage);
        UISyncConfig config = store.createNewConfig("admin", "some label1", "Redmine REST", "Microsoft Project");
        NewMappings newMappings = config.getNewMappings();
        Collection<FieldMapping> mappings = newMappings.getMappings();
        FieldMapping fieldMapping = findField(mappings, GTaskDescriptor.FIELD.START_DATE);
        assertEquals("Start date", fieldMapping.getConnector1());
        assertEquals("MUST_START_ON", fieldMapping.getConnector2());
        assertTrue(fieldMapping.isSelected());
    }

    private FieldMapping findField(Collection<FieldMapping> mappings, GTaskDescriptor.FIELD field) {
        for (FieldMapping mapping : mappings) {
            if (mapping.getField().equals(field)) {
                return mapping;
            }
        }
        return null;
    }
}
