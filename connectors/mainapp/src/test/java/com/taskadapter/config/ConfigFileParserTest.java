package com.taskadapter.config;

import com.taskadapter.PluginManager;
import com.taskadapter.util.MyIOUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigFileParserTest {
    @Test
    public void fileIsParsed() throws IOException {
        String contents = MyIOUtils.getResourceAsString("redmine.ta_conf");
        ConfigFileParser parser = new ConfigFileParser(new PluginManager());
        TAFile file = parser.parse(contents);

        assertEquals("Redmine DEMO", file.getConfigLabel());
        assertEquals("Redmine REST", file.getConnectorDataHolder1().getType());
        assertEquals("Microsoft Project", file.getConnectorDataHolder2().getType());
    }
}
