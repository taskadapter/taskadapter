package com.taskadapter.webui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LastVersionLoaderIntegrationTest {

    @Test
    public void lastVersionIsLoadedFromUpdateServer() {
        String availableVersion = LastVersionLoader.loadLastVersion();
        String[] strings = availableVersion.split("\r\n|\r|\n");
        // must be 1 line only
        assertEquals(1, strings.length);

        assertTrue(availableVersion.startsWith("3."));
    }
}
