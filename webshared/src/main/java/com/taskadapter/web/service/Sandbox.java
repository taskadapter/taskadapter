package com.taskadapter.web.service;

import java.io.File;

/**
 * Sandbox implementation.
 * 
 */
public final class Sandbox {
    private final boolean allowLocalFSAccess;
    private final File userContentDirectory;

    public Sandbox(boolean allowLocalFSAccess, File userFilesSandbox) {
        super();
        this.allowLocalFSAccess = allowLocalFSAccess;
        this.userContentDirectory = userFilesSandbox;
    }

    public boolean allowLocalFSAccess() {
        return allowLocalFSAccess;
    }

    public File getUserContentDirectory() {
        return userContentDirectory;
    }

}
