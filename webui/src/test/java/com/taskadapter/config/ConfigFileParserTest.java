package com.taskadapter.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConfigFileParserTest {
    @Test
    public void legacyConfigFileIsParsed() throws IOException {
        String contents = Resources.toString(Resources.getResource("redmine.ta_conf"), Charsets.UTF_8);
        StoredExportConfig file = NewConfigParser.parseLegacyConfig(1, contents);

        assertEquals("Redmine DEMO", file.getName());
        assertEquals("Redmine REST", file.getConnector1().getConnectorTypeId());
        assertEquals("Microsoft Project", file.getConnector2().getConnectorTypeId());
    }

    /**
     * configs with ID were introduced in Jan 2021.
     */
    @Test
    public void configWithIdFileIsParsed() throws IOException {
        String contents = Resources.toString(Resources.getResource("redmine.conf"), Charsets.UTF_8);
        StoredExportConfig file = NewConfigParser.parse(contents);

        assertEquals("Redmine DEMO", file.getName());
        assertEquals("Redmine REST", file.getConnector1().getConnectorTypeId());
        assertEquals("Microsoft Project", file.getConnector2().getConnectorTypeId());
    }
}
