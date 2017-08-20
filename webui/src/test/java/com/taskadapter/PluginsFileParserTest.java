package com.taskadapter;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PluginsFileParserTest {
    private static final String FILE_CONTENTS = "#some comment\n" +
            "connector1\n" +
            "#another comment\n" +
            "connector with space 2\n" +
            "com.connector 3";

    @Test
    public void threeItemsTotal() throws Exception {
        Collection<String> strings = new PluginsFileParser().parsePluginsFile(FILE_CONTENTS);
        assertEquals(3, strings.size());
    }

    @Test
    public void allConnectorsFound() throws Exception {
        Collection<String> strings = new PluginsFileParser().parsePluginsFile(FILE_CONTENTS);
        assertTrue(strings.contains("connector1"));
        assertTrue(strings.contains("connector with space 2"));
        assertTrue(strings.contains("com.connector 3"));
    }

    @Test
    public void noUsefulItemsResultInEmptyCollection() throws Exception {
        String fileContents = "#some comment\n" +
                "#another comment\n";
        assertTrue(new PluginsFileParser().parsePluginsFile(fileContents).isEmpty());
    }

    @Test
    public void doesNotFailWithEmptyFile() throws Exception {
        assertTrue(new PluginsFileParser().parsePluginsFile("").isEmpty());
    }

    @Test
    public void emptyLinesAreIgnored() throws Exception {
        String fileContents = "#some comment\n" +
                "connector 1\n" +
                "\n" +
                "\n" +
                "connector 2";
        Collection<String> strings = new PluginsFileParser().parsePluginsFile(fileContents);
        assertEquals(2, strings.size());
        assertTrue(strings.contains("connector 1"));
        assertTrue(strings.contains("connector 2"));
        assertFalse(strings.contains(""));
    }
}
