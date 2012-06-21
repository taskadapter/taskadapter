package com.taskadapter.web.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateManagerTest {

    private UpdateManager updateManager;

    @Before
    public void beforeEachMethod() {
        updateManager = new UpdateManager();
        String lastAvailable = "1.4.3";
        updateManager.setLastVersion(lastAvailable);
    }

    @Test
    public void somethingIsLoaded() {
        String version = updateManager.getLatestAvailableVersion();
        assertNotNull(version);
        System.out.println("Loaded the last available version number from the server: " + version);
        String[] strings = version.split("\r\n|\r|\n");
        // must be 1 line only
        assertEquals(1, strings.length);
    }

    @Test
    public void compareVersions1() {
        updateManager.setCurrentVersionForTesting("1.0");
        assertTrue(updateManager.isCurrentVersionOutdated());
    }

    @Test
    public void compareVersions2() {
        updateManager.setCurrentVersionForTesting("1.4.1");
        assertTrue(updateManager.isCurrentVersionOutdated());
    }

    @Test
    public void compareVersions3() {
        updateManager.setCurrentVersionForTesting("1.4.3");
        assertFalse(updateManager.isCurrentVersionOutdated());
    }

    @Test
    public void compareVersions4() {
        updateManager.setCurrentVersionForTesting("2.0.2");
        assertFalse(updateManager.isCurrentVersionOutdated());
    }
}
