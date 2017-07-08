package com.taskadapter.web.uiapi;

import com.taskadapter.model.GTaskDescriptor;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UISyncConfigTest {
/*
    @Test
    public void remoteIdIsRecognizedAsSelected() throws IOException {
        UISyncConfig uiSyncConfig = ConfigLoader.loadConfig("jira_msp.conf");

        assertTrue(uiSyncConfig.generateTargetMappings().isFieldSelected(GTaskDescriptor.FIELD.REMOTE_ID));
        assertTrue(uiSyncConfig.generateSourceMappings().isFieldSelected(GTaskDescriptor.FIELD.SUMMARY));
    }

*/
    @Test
    public void legacyConfigTA22LoadedWithoutNPE() throws IOException {
        UISyncConfig config = ConfigLoader.loadConfig("legacy_config_ta_2.2.txt");
        assertEquals("Some Config", config.getLabel());
    }

    @Test
    public void legacyConfigTA22WithNullRemoteIdLoadedWithoutNPE() throws IOException {
        UISyncConfig config = ConfigLoader.loadConfig("legacy_config_2.2_null_values.txt");
        assertEquals("Bogdan-2", config.getLabel());
    }
}
