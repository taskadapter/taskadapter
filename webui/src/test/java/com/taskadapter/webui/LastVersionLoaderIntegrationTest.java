package com.taskadapter.webui;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LastVersionLoaderIntegrationTest {

    /**
     * stopped working after migrating TaskAdapter website from http://www.taskadapter.com to https://taskadapter.com.
     * disabling for today to be able to release a new version. this check works fine in the app itself.
     */
    @Test
    public void lastVersionIsLoadedFromUpdateServer() {
        String availableVersion = LastVersionLoader.loadLastVersion();
        String[] strings = availableVersion.split("\r\n|\r|\n");
        // must be 1 line only
        assertEquals(1, strings.length);

        assertTrue("last version tag loaded from TA site must start with 3, but it was: '" + availableVersion + "'",
                availableVersion.startsWith("3."));
    }
}
