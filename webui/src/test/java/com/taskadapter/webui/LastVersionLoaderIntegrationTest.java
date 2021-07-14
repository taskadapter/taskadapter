package com.taskadapter.webui;

import com.taskadapter.test.core.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class LastVersionLoaderIntegrationTest {

    /**
     * this test is no longer relevant after open-sourcing the app. there will be no more "official" version from now on.
     */
    @Ignore
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
