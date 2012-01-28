package com.taskadapter.webui;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateManagerTest {
    @Test
    public void somethingIsLoaded() {
        UpdateManager updateManager = new UpdateManager();
        String version = updateManager.getLatestAvailableVersion();
        assertNotNull(version);
        System.out.println("Loaded the last available version number from the server: " + version);
        String[] strings = version.split("\r\n|\r|\n");
        // must be 1 line only
        assertEquals(1, strings.length);
    }

    @Test
    public void compareVersions() {

        String lastAvailable = "1.4.3";

        assertTrue(UpdateManager.isOutdated("1.0", lastAvailable));
        assertTrue(UpdateManager.isOutdated("1.4.1", lastAvailable));
        assertFalse(UpdateManager.isOutdated("1.4.3", lastAvailable));
        assertFalse(UpdateManager.isOutdated("1.4.3", lastAvailable));
        assertFalse(UpdateManager.isOutdated("2.0.0", lastAvailable));
    }
}
