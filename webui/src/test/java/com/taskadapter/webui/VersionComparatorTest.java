package com.taskadapter.webui;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VersionComparatorTest {

    @Test
    public void compareVersions1() {
        assertTrue(VersionComparator.isCurrentVersionOutdated("2.0", "2.4.3"));
    }

    @Test
    public void compareVersions2() {
        assertTrue(VersionComparator.isCurrentVersionOutdated("2.4.1", "2.4.3"));
    }

    @Test
    public void sameVersionIsNotOutdated() {
        assertFalse(VersionComparator.isCurrentVersionOutdated("2.4.3", "2.4.3"));
    }

    @Test
    public void compareVersions4() {
        assertTrue(VersionComparator.isCurrentVersionOutdated("2.0.1", "2.4.3"));
    }
}
