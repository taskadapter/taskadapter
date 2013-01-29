package com.taskadapter.web.service;

import java.io.File;

/**
 * Advisory "sandbox" API.
 * <p>
 * Sandbox allows plugins to work in an environment, where access to some
 * features (like a filesystem acces, system configuration, etc...) may be
 * restricted to prevent unauthorized access. For example, plugin may restrict
 * access to a filesystem if connector is running on a server or in a multi-user
 * mode.
 * <p>
 * <strong>Sandboxing does not prevent plugins from performing "unauthorized
 * actions". However, usage of a sandbox API is strongly suggested whenever
 * possible.</strong>
 * 
 */
// FIXME: refactor this to a final class!!!
public interface Sandbox {
    /**
     * Checks, if an access to local filesystem is allowed. "Local filesystem"
     * is a filesystem on a server with a plugin running.
     */
    public boolean allowLocalFSAccess();

    /**
     * Returns a user content directory for a "current" user. It is guaranteed,
     * that no configuration or system files will be in this folder. Thus,
     * plugins may performs any actions with files inside this folder. However,
     * many plugins may access a same folder and some cooperation is neccessary
     * to ensure, that no problem occurs.
     * 
     * @return user content directory.
     */
    public File getUserContentDirectory();
}
