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
        TAConfig config = parser.parse(contents);

        assertEquals("Redmine DEMO", config.getName());
        assertEquals("Redmine REST", config.getConnector1().getType());
        assertEquals("Microsoft Project", config.getConnector2().getType());
    }
}
