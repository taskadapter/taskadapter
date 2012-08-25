package com.taskadapter.config;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.PluginManager;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ConfigFileParserTest {
    @Test
    public void fileIsParsed() throws IOException {
        String contents = Resources.toString(Resources.getResource("redmine.ta_conf"), Charsets.UTF_8);
        ConfigFileParser parser = new ConfigFileParser(new PluginManager());
        TAFile file = parser.parse(contents);

        assertEquals("Redmine DEMO", file.getConfigLabel());
        assertEquals("Redmine REST", file.getConnectorDataHolder1().getType());
        assertEquals("Microsoft Project", file.getConnectorDataHolder2().getType());
    }
}
