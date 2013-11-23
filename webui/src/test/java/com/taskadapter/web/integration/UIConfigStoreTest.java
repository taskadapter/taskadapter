package com.taskadapter.web.integration;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.service.EditorManager;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UIConfigStoreTest extends FileBasedTest {
    @Test
    public void configCreatedWithProperDefaultMappings() throws Exception {
        UISyncConfig config = getStore().createNewConfig("admin", "label1", "Redmine REST", "Microsoft Project");
        checkFieldSelected(config.getNewMappings(), "Start Date", "MUST_START_ON");
    }

    private void checkFieldSelected(NewMappings newMappings, String connector1ExpectedValue, String connector2ExpectedValue) {
        FieldMapping fieldMapping = findField(newMappings.getMappings(), GTaskDescriptor.FIELD.START_DATE);
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
    }

    private UIConfigStore getStore() {
        final ConfigStorage configStorage = new ConfigStorage(tempFolder);

        EditorManager editorManager = EditorManager.fromResource("editors.txt");
        UIConfigService uiConfigService = new UIConfigService(new PluginManager(), editorManager);

        return new UIConfigStore(uiConfigService, configStorage);
    }
}
