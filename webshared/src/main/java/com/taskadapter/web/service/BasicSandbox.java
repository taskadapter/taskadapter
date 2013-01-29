package com.taskadapter.web.service;

import java.io.File;

/**
 * Sandbox implementation.
 * 
 */
public final class BasicSandbox implements Sandbox {
    private final boolean allowLocalFSAccess;
    private final File userContentDirectory;

    public BasicSandbox(boolean allowLocalFSAccess, File userFilesSandbox) {
        super();
        this.allowLocalFSAccess = allowLocalFSAccess;
        this.userContentDirectory = userFilesSandbox;
    }

    @Override
    public boolean allowLocalFSAccess() {
        return allowLocalFSAccess;
    }

    @Override
    public File getUserContentDirectory() {
        return userContentDirectory;
    }

}
