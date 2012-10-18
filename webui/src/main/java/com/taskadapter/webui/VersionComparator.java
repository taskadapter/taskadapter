package com.taskadapter.webui;

/**
 * Compare Task Adapter application version. Used in the User Interface to show outdated message warning.
 */
public class VersionComparator {
    public static boolean isCurrentVersionOutdated(String currentVersion, String lastAvailableVersion) {
        int res = currentVersion.compareTo(lastAvailableVersion);
        return res < 0;
    }
}
