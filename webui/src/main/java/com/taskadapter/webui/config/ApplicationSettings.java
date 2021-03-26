package com.taskadapter.webui.config;

import java.io.File;

public class ApplicationSettings {

    /**
     * Calculates default taskadapter root folder. Current implementation
     * returns <code>user.home/.taskadapter</code>.
     *
     * @return task adapter config folder.
     */
    public static File getDefaultRootFolder() {
        var userHome = System.getProperty("user.home");
        return new File(userHome, ".taskadapter");
    }
}
