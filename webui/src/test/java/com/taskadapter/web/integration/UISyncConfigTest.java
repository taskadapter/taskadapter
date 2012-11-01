package com.taskadapter.web.integration;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.PluginManager;
import com.taskadapter.config.NewConfigParser;
import com.taskadapter.config.StoredExportConfig;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.uiapi.UIConfigService;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.web.uiapi.UISyncConfigBuilder;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UISyncConfigTest {
    @Test
    public void remoteIdIsRecognizedAsSelected() throws IOException {
        UISyncConfig uiSyncConfig = getConfig("jira_msp.conf");

        assertTrue(uiSyncConfig.generateTargetMappings().isFieldSelected(GTaskDescriptor.FIELD.REMOTE_ID));
        assertTrue(uiSyncConfig.generateSourceMappings().isFieldSelected(GTaskDescriptor.FIELD.SUMMARY));
    }

    @Test
    public void legacyConfigLoadedWithoutNPE() throws IOException {
        UISyncConfig config = getConfig("legacy_config_ta_2.2.txt");
        assertEquals("Some Config", config.getLabel());
    }

    private UISyncConfig getConfig(String resourceNameInClassPath) throws IOException {
        String contents = Resources.toString(Resources.getResource(resourceNameInClassPath), Charsets.UTF_8);
        StoredExportConfig config = NewConfigParser.parse("someId", contents);

        EditorManager editorManager = EditorManager.fromResource("editors.txt");
        UIConfigService uiConfigService = new UIConfigService(new PluginManager(), editorManager);
        UISyncConfigBuilder builder = new UISyncConfigBuilder(uiConfigService);
        return builder.uize(config);
    }


}
